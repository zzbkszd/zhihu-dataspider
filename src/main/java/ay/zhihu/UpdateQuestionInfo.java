package ay.zhihu;

import ay.common.http.proxy.ProxyDaemon;
import ay.common.http.proxy.ProxyPool;
import ay.common.jdbc.DBUtils;
import ay.spider.SpiderContext;
import ay.spider.thread.Dist;
import ay.spider.thread.WatchedThread;
import ay.zhihu.pojo.Question;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public class UpdateQuestionInfo extends WatchedThread<Void,Void>{

    Log LOG = LogFactory.getLog(UpdateQuestionInfo.class);

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        SpiderContext context = new SpiderContext();
        ProxyDaemon proxyDaemon = context.enableProxyDeamon();
        while(ProxyPool.size()<20){
            try {
                Thread.sleep(1000);
                System.out.println(proxyDaemon.getWatchedReport());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        context.enableWatchReport();
        //更新的线程
        context.createChan().append(new UpdateQuestionInfo(context,true));
        context.startUp();
    }
    private int page = 0;
    private AtomicInteger count= new AtomicInteger(0);

    public UpdateQuestionInfo(SpiderContext context, boolean isroot) {
        super(context, isroot);
    }

    public UpdateQuestionInfo(SpiderContext context) {
        super(context);
    }


    @Override
    public boolean process(Optional<Void> in, Optional<Dist<Void>> out) {

        RequestCenter requestCenter = new RequestCenter();
        DBUtils dbUtils = DBUtils.getMysqlIns();
        List<Question> questions = null;
        try {
            questions = dbUtils.queryOrm("select * from question where attention is null limit ?,1000",(page*1000)).to(Question.class);
            for (Question question : questions) {
                getCtx().execTask(new Update(question,requestCenter,dbUtils));
            }
            LOG.info("update page "+page+" success");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        page++;
        return questions.size()>0;
    }

    @Override
    public String getWatchedReport() {
        StringBuilder report = new StringBuilder();
        report.append("\t updating page:").append(page).append("\n")
                .append("\t updated count:").append(count.get()).append("\n");
        return report.toString();
    }

    class Update implements Runnable{
        private Question question;
        private RequestCenter center;
        private DBUtils dbUtils;
        public Update(Question question,RequestCenter center,DBUtils dbUtils){
            this.question = question;
            this.center = new RequestCenter();
            this.dbUtils = dbUtils;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Question q = center.getQuestionDetail(question.getQuestionId());
            if(q==null){
                LOG.error("null question:"+question.getTitle());
                return;
            }
            try {
                if(q.getTopics()!=null){
                    dbUtils.update("update question set topics=?,description=?,answers=?,attention=?,view=?,updatetime=? where questionId=?",
                            q.getTopics(),q.getDescription(),q.getAnswers(), q.getAttention(),
                            q.getView(),new Timestamp(System.currentTimeMillis()),question.getQuestionId());
                    count.incrementAndGet();
                    long end = System.currentTimeMillis();
                    LOG.info("update question "+question.getTitle()+" success in "+(end-start)+" millis seconds");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package ay.common.http;

import ay.common.http.proxy.ProxyDaemon;
import ay.common.jdbc.DBUtils;
import ay.spider.SpiderContext;
import ay.spider.thread.Dist;
import ay.spider.thread.WatchedThread;
import ay.zhihu.RequestCenter;
import ay.zhihu.RouterCenter;
import ay.zhihu.pojo.Question;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
        context.execTask(new ProxyDaemon(context,true));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //更新的线程
        context.createChan().append(new UpdateQuestionInfo(context,true));
        context.startUp();
    }
    private int page = 0;

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
//                Question q = requestCenter.getQuestionDetail(question.getQuestionId());
//                dbUtils.update("update question set topics=?,description=?,answers=?,attention=?,view=?,updatetime=? where questionId=?",
//                        q.getTopics(),q.getDescription(),q.getAnswers(), q.getAttention(),
//                        q.getView(),new Timestamp(System.currentTimeMillis()),q.getQuestionId());
//                LOG.info("update question "+q.getTitle()+" success");
                getCtx().execTask(new Update(question,requestCenter,dbUtils));
            }
            LOG.info("update page "+page+" success");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        page++;
        return questions.size()>0;
    }

    class Update implements Runnable{
        private Question question;
        private RequestCenter center;
        private DBUtils dbUtils;
        public Update(Question question,RequestCenter center,DBUtils dbUtils){
            this.question = question;
            this.center = center;
            this.dbUtils = dbUtils;
        }

        @Override
        public void run() {
            Question q = center.getQuestionDetail(question.getQuestionId());
            try {
                dbUtils.update("update question set topics=?,description=?,answers=?,attention=?,view=?,updatetime=? where questionId=?",
                        q.getTopics(),q.getDescription(),q.getAnswers(), q.getAttention(),
                        q.getView(),new Timestamp(System.currentTimeMillis()),question.getQuestionId());
                LOG.info("update question "+q.getTitle()+" success");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

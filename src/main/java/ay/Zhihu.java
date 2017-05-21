package ay;
import ay.common.http.HttpUtil;
import ay.common.http.ProxyHttpClient;
import ay.common.jdbc.DBUtils;
import ay.spider.ZhihuSpiderContext;
import ay.zhihu.RequestCenter;
import ay.zhihu.api.Answers;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Question;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by Ay on 2017/4/9.
 */
public class Zhihu {
    static Log LOG = LogFactory.getLog(Zhihu.class);
    public static void main(String[] args) {
//        RequestCenter requestCenter = new RequestCenter();
//        Question question = requestCenter.getQuestionDetail(27301322);
//        System.out.println(question.getTitle());
//        System.out.println(question.getDescription());
//        System.out.println(question.getTopics());
        updateQuestion();
    }

    public static void updateQuestion(){
        DBUtils dbUtils = DBUtils.getMysqlIns();
        RequestCenter requestCenter = new RequestCenter();
        int page=0;
        try {
            List<Map<String,Object>> questions = dbUtils.query("select questionId from question where questionId > 19661544 limit ?,1000",page*1000);
            while(questions.size()>0){
                for (Map<String, Object> q: questions) {
                    Question question = requestCenter.getQuestionDetail((Integer) q.get("questionId"));
                    if(question==null)
                        continue;
                    dbUtils.update("update question set topics=?,description=?,answers=?,attention=?,view=?,updatetime=? where questionId=?",
                            question.getTopics(),question.getDescription(),question.getAnswers(), question.getAttention(),
                            question.getView(),new Timestamp(System.currentTimeMillis()),question.getId());
                }
                LOG.info("update question info by page :"+page);
                page++;
                questions = dbUtils.query("select questionId from question limit ?,1000",page*1000);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void start() throws IOException {
        //配置日志
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        //开始工作
        new ZhihuSpiderContext(true).work("xie-huang-rui");
    }
    public static void cleanDB() {
        DBUtils dbUtils = DBUtils.getMysqlIns();
        try {
            List<Map<String,Object>> questions = dbUtils.query("select question.id,question.questionId,t.ac from (SELECT count(*) ac,questionId FROM zhihu.question group by questionId) t right join question on t.questionId=question.questionId where ac>1 order by question.questionId desc");
            List<Integer> fordel = new ArrayList<>();

            for(int i=0;i<questions.size();i+=2){
                Map<String,Object> q = questions.get(i);
                long ac = (long) q.get("ac");
                if(ac==3){
                    i+=1;
                }
                int qid = (int) q.get("questionId");
                Map<String,Object> dq = questions.get(i+1);
                int dqid = (int) dq.get("questionId");
                if(qid!=dqid){
                    System.out.println("exception! qid="+qid+" and dqid="+dqid+" ac="+q.get("ac"));
                    break;
                }
                int delid = (int) dq.get("id");
                fordel.add(delid);
            }
            System.out.println("del size:"+fordel.size());
            Integer[][] param = new Integer[fordel.size()][1];
            for(int i=0;i<fordel.size();i++) param[i][0] = fordel.get(i);
            dbUtils.getRunner().batch("delete from question where id=?",param);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}

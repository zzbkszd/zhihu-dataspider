package ay;
import ay.jdbc.DBUtils;
import ay.spider.ZhihuSpiderContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Ay on 2017/4/9.
 */
public class Zhihu {

    public static void main(String[] args) {
        try {
            start();
        } catch (IOException e) {
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

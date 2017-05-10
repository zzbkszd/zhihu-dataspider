package ay;
import ay.common.http.HttpUtil;
import ay.common.jdbc.DBUtils;
import ay.spider.ZhihuSpiderContext;
import ay.zhihu.api.Answers;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Question;

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

    public static void main(String[] args) {
        Integer[] arr = new Integer[]{1,2,3,0,0,0,5,6,0,0,8};
        Stream<Integer> integerStream = Stream.of(arr);
        Arrays.stream(arr);
        Integer[] output = (Integer[]) integerStream.filter(a->a!=0).collect(Collectors.toList()).toArray(new Integer[0]);
        for (Integer integer : output) {
            System.out.println(integer);
        }

//           updateQuestion();
//        try {
//            HttpUtil http = new HttpUtil();
//            String str = http.get("http://www.zhihu.com/api/v4/questions/57981549");
//            System.out.println(str);
////            updateQuestion();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void updateQuestion(){
        HttpUtil http = new HttpUtil();
        DBUtils dbUtils = DBUtils.getMysqlIns();
        int page=0;
        try {
            List<Map<String,Object>> questions = dbUtils.query("select questionId from question where questionId > 19661544 limit ?,1000",page*1000);
            while(questions.size()>0){
                for (Map<String, Object> q: questions) {
                    Question question = QuestionApi.questionInfo(http, (Integer) q.get("questionId"));
                    if(question==null)
                        continue;
                    dbUtils.update("update question set topics=?,description=?,answers=?,attention=?,view=?,updatetime=? where questionId=?",
                            question.getTopics(),question.getDescription(),question.getAnswers(), question.getAttention(),
                            question.getView(),new Timestamp(System.currentTimeMillis()),question.getId());
                }
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

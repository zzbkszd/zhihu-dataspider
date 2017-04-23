package ay.spider;

import ay.jdbc.DBUtils;
import ay.zhihu.RequestCenter;
import ay.zhihu.pojo.Answer;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 抓取用户的所有答案
 * Created by 志达 on 2017/4/16.
 */
public class AnswerThread implements Runnable {
    RequestCenter requestCenter = new RequestCenter();
    DBUtils dbUtils = new DBUtils();
    ArrayBlockingQueue<String> taskQueue;
    List<Object[]> answerCache = new ArrayList<>(); //线程内数据缓存，用于批量插入数据库
    List<Object[]> questionCache = new ArrayList<>(); //线程内数据缓存，用于批量插入数据库
    boolean useCache = false;

    public AnswerThread(ArrayBlockingQueue taskQueue){
        this.taskQueue = taskQueue;
    }

    public AnswerThread(ArrayBlockingQueue taskQueue,boolean useCache){
        this.taskQueue = taskQueue;
        this.useCache = useCache;
    }

    @Override
    public void run() {
        while(true){
            try {
                String token = taskQueue.take();
                if(StringUtils.isEmpty(token)){
                    continue;
                }
                List<JsonObject> answers = requestCenter.getAllAnswer(token);
                for (JsonObject answer : answers) {
                    JsonObject question = answer.getAsJsonObject("question");
                    int questionId = question.get("id").getAsInt();
                    String title = question.get("title").getAsString();
                    //添加问题
                    if(hasQuestion(questionId)){
                        long created = question.get("created").getAsLong();
                        questionCache.add(new Object[]{title,questionId,new Timestamp(created)});

                        if(useCache){
                            DataCache.getInstant().lset(DataCache.KEY_QUESTION_DIS,questionId);
                        }
                    }

                    int answerId = answer.get("id").getAsInt();

                    if(hasAnswer(answerId)){
                        continue;
                    }

                    int voteup = answer.get("voteup_count").getAsInt();
                    int comment = answer.get("comment_count").getAsInt();
                    String content = answer.get("content").getAsString();
                    long createTime = answer.get("created_time").getAsLong();


                    JsonObject author = answer.getAsJsonObject("author");
                    String authorId = author.get("id").getAsString();

                    answerCache.add(new Object[]{questionId,content,authorId,answerId,new Timestamp(createTime*1000),voteup,comment});
                    if(useCache){
                        DataCache.getInstant().lset(DataCache.KEY_ANSWER_DIS,answerId);
                    }

                }
                dbUtils.batchInsert("insert into answer(questionId,content,authorId,answerId,created_time,voteup_count,comment_count) values (?,?,?,?,?,?,?)"
                        ,answerCache);
                System.out.println("保存答案信息完成："+answerCache.size());
                dbUtils.batchInsert("insert into question(title,questionId,created) values (?,?,?)",questionCache);
                System.out.println("保存问题信息完成："+questionCache.size());
                answerCache.clear();
                questionCache.clear();

            } catch (IOException | SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasQuestion(int questionId) throws SQLException{
        if(useCache){
            return DataCache.getInstant().lin(DataCache.KEY_QUESTION_DIS,questionId);
        }else{
            return dbUtils.query("select id from question where questionId=?",questionId).size()==0;
        }

    }

    private boolean hasAnswer(int answerId) throws SQLException {
        if(!useCache)
            return (dbUtils.query("select id from answer where answerId=?",answerId).size()!=0);
        else{
            return DataCache.getInstant().lin(DataCache.KEY_ANSWER_DIS,answerId);
        }
    }
}

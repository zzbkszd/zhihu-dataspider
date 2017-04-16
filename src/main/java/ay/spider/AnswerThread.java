package ay.spider;

import ay.jdbc.DBUtils;
import ay.zhihu.RequestCenter;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class AnswerThread implements Runnable {
    DataCache dataCache = DataCache.getInstant();
    RequestCenter requestCenter = new RequestCenter();
    DBUtils dbUtils = new DBUtils();
    ArrayBlockingQueue<String> taskQueue;

    public AnswerThread(ArrayBlockingQueue taskQueue){
        this.taskQueue = taskQueue;
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
//                    System.out.println(answer);
                    JsonObject question = answer.getAsJsonObject("question");
                    int questionId = question.get("id").getAsInt();
                    String title = question.get("title").getAsString();
                    //添加问题
                    if(!dataCache.lin("question_ids",questionId)){
                        long created = question.get("created").getAsLong();
//                        System.out.println("saving question:"+title);
                        dbUtils.insert("insert into question(title,questionId,created) values (?,?,?)",
                                title,questionId,new Timestamp(created));
                        dataCache.lset("question_ids",questionId);
                    }

                    int answerId = answer.get("id").getAsInt();
                    int voteup = answer.get("voteup_count").getAsInt();
                    int comment = answer.get("comment_count").getAsInt();
                    String content = answer.get("content").getAsString();
                    long createTime = answer.get("created_time").getAsLong();


                    JsonObject author = answer.getAsJsonObject("author");
                    String authorId = author.get("id").getAsString();

//                    System.out.println("saving answer id "+answerId+" for question "+title);

                    dbUtils.insert("insert into answer(questionId,content,authorId,answerId,created_time,voteup_count,comment_count) values (?,?,?,?,?,?,?)",
                            questionId,content,authorId,answerId,new Timestamp(createTime),voteup,comment);

                }
            } catch (IOException e) {
            } catch (SQLException e) {
            } catch (InterruptedException e) {
            }
        }
    }
}

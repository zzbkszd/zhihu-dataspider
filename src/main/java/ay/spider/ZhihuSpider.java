package ay.spider;

import ay.jdbc.DBUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class ZhihuSpider {


    DataCache cache;

    public ZhihuSpider(){
        cache = DataCache.getInstant();
        try {
            initCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void work(String rootUser){

        ArrayBlockingQueue<String> taskQueue = new ArrayBlockingQueue<>(10);

        Thread thread = new Thread(new UserInfoThread(taskQueue,rootUser));
        thread.start();
        Thread answer = new Thread(new AnswerThread(taskQueue));
        answer.start();

    }

    public void initCache() throws SQLException {

        DBUtils dbUtils = new DBUtils();

        List<Map<String,Object>> users = dbUtils.query("select urlToken from user");
        for (Map<String, Object> user : users) {
            cache.lset("user_token_list",user.get("urlToken"));
        }

        List<Map<String,Object>> questions = dbUtils.query("select questionId from question");
        for (Map<String, Object> question : questions) {
            cache.lset("question_ids",question.get("questionId"));
        }


        List<Map<String,Object>> answers = dbUtils.query("select answerId from answer");
        for (Map<String, Object> question : questions) {
            cache.lset("answer_ids",question.get("answerId"));
        }


    }





}

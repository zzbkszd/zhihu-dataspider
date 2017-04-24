package ay.spider;

import ay.jdbc.DBUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class ZhihuSpiderContext {


    DataCache cache;

    boolean useCache = false;
    ArrayBlockingQueue<String> user4FollowsQueue = new ArrayBlockingQueue<>(10);
    ArrayBlockingQueue<String> user4AnswersQueue = new ArrayBlockingQueue<>(10);

    public DataCache getCache() {
        return cache;
    }

    public void setCache(DataCache cache) {
        this.cache = cache;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public ArrayBlockingQueue<String> getUser4FollowsQueue() {
        return user4FollowsQueue;
    }

    public void setUser4FollowsQueue(ArrayBlockingQueue<String> user4FollowsQueue) {
        this.user4FollowsQueue = user4FollowsQueue;
    }

    public ArrayBlockingQueue<String> getUser4AnswersQueue() {
        return user4AnswersQueue;
    }

    public void setUser4AnswersQueue(ArrayBlockingQueue<String> user4AnswersQueue) {
        this.user4AnswersQueue = user4AnswersQueue;
    }

    public ZhihuSpiderContext(boolean useCache){
        cache = DataCache.getInstant();
        this.useCache = useCache;
        try {
            initCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void work(String rootUser){

        try {
            user4FollowsQueue.put(rootUser);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new UserInfoThread(this));
        thread.start();
        Thread answer = new Thread(new AnswerThread(this));
        answer.start();

    }

    public void initCache() throws SQLException {

        if(!useCache){
            return;
        }

        DBUtils dbUtils = new DBUtils();
        System.out.println("开始初始化缓存，可能消耗很长时间");

        List<Map<String,Object>> users = dbUtils.query("select urlToken from user");
        for (Map<String, Object> user : users) {
            cache.lset(DataCache.KEY_USER_DIS,user.get("uuid"));
        }
        users = null;
        System.out.println("用户缓存初始化完成");

        List<Map<String,Object>> questions = dbUtils.query("select questionId from question");
        for (Map<String, Object> question : questions) {
            cache.lset(DataCache.KEY_QUESTION_DIS,question.get("questionId"));
        }
        questions = null;
        System.out.println("问题缓存初始化完成");


        List<Map<String,Object>> answers = dbUtils.query("select answerId from answer");
        for (Map<String, Object> answer : answers) {
            cache.lset(DataCache.KEY_ANSWER_DIS,answer.get("answerId"));
        }
        answers = null;
        System.out.println("答案缓存初始化完成");

        dbUtils = null;

        System.gc();

    }





}

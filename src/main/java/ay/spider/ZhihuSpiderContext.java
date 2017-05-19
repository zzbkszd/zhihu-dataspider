package ay.spider;

import ay.common.jdbc.DBUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class ZhihuSpiderContext {

    boolean useCache = false;
    ArrayList<String> user4FollowsQueue = new ArrayList<>();//bigger then bigger
    ArrayBlockingQueue<String> user4AnswersQueue = new ArrayBlockingQueue<>(10);

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public ArrayList<String> getUser4FollowsQueue() {
        return user4FollowsQueue;
    }

    public ArrayBlockingQueue<String> getUser4AnswersQueue() {
        return user4AnswersQueue;
    }

    public ZhihuSpiderContext(boolean useCache){
        this.useCache = useCache;
        try {
            initCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void work(String rootUser){

        user4FollowsQueue.add(rootUser);
        //此处应当考虑如何实现工作流式调度（线程编排）
        //任务一-》任务2-》任务3-》任务4，通过统一的API从context中获取任务，交付任务
        //可以考虑参考类似于netty的过滤器模式？
        //数据队列应该支持多种实现，诸如内存队列，消息队列等
        //如何实现一下任务监控，统一日志等工作
        //全局资源应当放在context中进行管理

        Thread thread = new Thread(new UserInfoThread(this));
        thread.start();
        Thread answer = new Thread(new AnswerThread(this));
        answer.start();

    }

    public void initCache() throws SQLException {

        if(!useCache){
            return;
        }

        DBUtils dbUtils = DBUtils.getMysqlIns();
        DBUtils hsqlUtils = DBUtils.getHsqlIns();
        System.out.println("开始初始化缓存，可能消耗很长时间");

        List<Map<String,Object>> tempData = dbUtils.query("select uuid from user");
        List<Object[]> params = new ArrayList<>();
        for (Map<String, Object> temp : tempData) {
            params.add(new Object[]{temp.get("uuid")});
            if(params.size()>10000){
                hsqlUtils.batchInsert("insert into cache_user(uuid) values (?)",params);
                params.clear();
            }
        }
        hsqlUtils.batchInsert("insert into cache_user(uuid) values (?)",params);
        params.clear();
        System.out.println("用户缓存初始化完成");

        tempData = dbUtils.query("select questionId from question");
        for (Map<String, Object> temp : tempData) {
            params.add(new Object[]{temp.get("questionId")});
            if(params.size()>10000){
                hsqlUtils.batchInsert("insert into cache_question(qid) values (?)",params);
                params.clear();
            }
        }
        hsqlUtils.batchInsert("insert into cache_question(qid) values (?)",params);
        params.clear();
        System.out.println("问题缓存初始化完成");


        tempData = dbUtils.query("select answerId from answer");
        for (Map<String, Object> temp : tempData) {
            params.add(new Object[]{temp.get("answerId")});
            if(params.size()>10000){
                hsqlUtils.batchInsert("insert into cache_answer(aid) values (?)",params);
                params.clear();
            }
        }
        hsqlUtils.batchInsert("insert into cache_answer(aid) values (?)",params);
        params.clear();
        System.out.println("答案缓存初始化完成");

    }





}

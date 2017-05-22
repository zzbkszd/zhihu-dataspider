package ay.zhihu;

import ay.common.http.proxy.ProxyDaemon;
import ay.common.jdbc.DBUtils;
import ay.spider.SpiderContext;
import ay.spider.thread.WatchedThread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class ZhihuSpiderContext {

    SpiderContext context;

    boolean useCache = false;

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public ZhihuSpiderContext(boolean useCache){
        this.useCache = useCache;
        try {
            initCache();
            context = new SpiderContext();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void work(){
        context.execTask(new ProxyDaemon(context,true));
        //更新的线程
        context.createChan().append(new WatchedThread<Void,Void>(context,true) {
            @Override
            public boolean process(Optional in, Optional out) {

                return false;
            }
        });
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

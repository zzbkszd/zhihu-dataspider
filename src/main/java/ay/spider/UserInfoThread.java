package ay.spider;

import ay.jdbc.DBUtils;
import ay.zhihu.RequestCenter;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 志达 on 2017/4/16.
 */
public class UserInfoThread implements Runnable{

    private RequestCenter requestCenter = new RequestCenter();
    private DBUtils dbUtils = new DBUtils();

    private ZhihuSpiderContext context;

    private List<Object[]> userCache = new ArrayList<>();

    public UserInfoThread(ZhihuSpiderContext context){
        this.context = context;
    }

    @Override
    public void run() {
        while(true){
            try {
                String ut = context.getUser4FollowsQueue().poll();
                if(ut==null && context.getUser4FollowsQueue().size()==0){
                    System.out.println("no more user!");
                    break;
                }
                else if(ut==null){
                    continue;
                }
                List<JsonObject> followees = requestCenter.getAllFollowees(ut);
                for (JsonObject followee : followees) {
                    String token = followee.get("url_token").getAsString();
                    String name = followee.get("name").getAsString();
                    String usertype = followee.get("user_type").getAsString();
                    String url = followee.get("url").getAsString();
                    String uuid = followee.get("id").getAsString();
                    int answercount = followee.get("answer_count").getAsInt();
                    int followerCount = followee.get("follower_count").getAsInt();

                    if(hasUser(uuid)){
                        continue;
                    }

                    if(StringUtils.isEmpty(token)){
                        continue;
                    }

                    if(context.getCache()!=null)
                    context.getUser4AnswersQueue().put(token);


                    context.getUser4FollowsQueue().add(token);

                    userCache.add(new Object[]{name,usertype,answercount,token,url,followerCount,uuid});
                    if(context.isUseCache()){
                        DataCache.getInstant().lset(DataCache.KEY_USER_DIS,uuid);
                    }

                }
                dbUtils.batchInsert("insert into user(name,usertype,answercount,urlToken,url,followerCount,uuid) values (?,?,?,?,?,?,?)",
                        userCache);
                userCache.clear();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean hasUser(String uuid) throws SQLException {
        if(context.isUseCache()){
            return DataCache.getInstant().lin(DataCache.KEY_USER_DIS,uuid);
        }else{
            return dbUtils.query("select id from user where urlToken=?",uuid).size()>0;
        }
    }

}

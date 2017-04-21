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

    RequestCenter requestCenter = new RequestCenter();
    DBUtils dbUtils = new DBUtils();
    ArrayBlockingQueue<String> taskQueue;
    String rootUser;

    public UserInfoThread(ArrayBlockingQueue taskQueue, String rootUser){
        this.taskQueue = taskQueue;
        this.rootUser = rootUser;
    }

    @Override
    public void run() {
        Queue<String> users = new LinkedList<>();
        users.add(rootUser);
        while(true){
            try {
                String ut = users.poll();
                if(ut==null && users.size()==0){
                    System.out.println("no more user!");
                    break;
                }
                else if(ut==null){
                    continue;
                }
                List<JsonObject> followees = requestCenter.getAllFollowees(ut);
                System.out.println(followees);
                for (JsonObject followee : followees) {
                    String token = followee.get("url_token").getAsString();
                    String name = followee.get("name").getAsString();
                    String usertype = followee.get("user_type").getAsString();
                    String url = followee.get("url").getAsString();
                    String uuid = followee.get("id").getAsString();
                    int answercount = followee.get("answer_count").getAsInt();
                    int followerCount = followee.get("follower_count").getAsInt();

                    if(dbUtils.query("select id from user where urlToken=?",token).size()>0){
                        continue;
                    }

                    if(StringUtils.isEmpty(token)){
                        continue;
                    }

                    if(taskQueue!=null)
                    taskQueue.put(token);


                    users.add(token);

    //                    System.out.println("saving user "+name);

                    dbUtils.insert("insert into user(name,usertype,answercount,urlToken,url,followerCount,uuid) values (?,?,?,?,?,?,?)",
                            name,usertype,answercount,token,url,followerCount,uuid);

            }
//                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

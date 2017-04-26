package ay.zhihu;

import ay.common.http.HttpUtil;
import ay.zhihu.api.Answers;
import ay.zhihu.api.Followers;
import ay.zhihu.api.nologin.NLFollowers;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

/**
 * 请求中心
 * Created by 志达 on 2017/4/9.
 */
public class RequestCenter {

    HttpUtil http = new HttpUtil();

    public List<JsonObject> getAllFollowees(String userKey) throws IOException {
        return Followers.followees(http,userKey);
    }

    public List<JsonObject> getAllAnswer(String userKey) throws IOException {
        return Answers.answerForUser(http,userKey);
    }

    public List<JsonObject> getAllFollowingByHtml(String userKey) throws IOException {
        return NLFollowers.followers(http,userKey);
    }

}

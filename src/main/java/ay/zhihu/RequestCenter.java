package ay.zhihu;

import ay.common.http.ProxyHttpClient;
import ay.common.http.StaticCookieJar;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import ay.common.util.CommonConfig;
import ay.zhihu.api.ApiDecoder;
import ay.zhihu.api.PagedApi;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Question;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 请求中心
 * Created by 志达 on 2017/4/9.
 */
public class RequestCenter {


    ProxyHttpClient proxyHttpClient = new ProxyHttpClient();

    public List<JsonObject> getAllFollowees(String userKey) throws IOException {
        return requestAllPage(userKey,RouterCenter::getFolloweesApi,RequestCenter::pagedDataDecoder);
    }

    public List<JsonObject> getAllAnswer(String userKey) throws IOException {
        return requestAllPage(userKey,RouterCenter::getAnswersApi,RequestCenter::pagedDataDecoder);
    }

    public Question getQuestionDetail(int questionId){
        String url = "https://www.zhihu.com/question/"+questionId;
        String http = requestData(url);
        if(http==null){
            return null;
        }
        Question question = QuestionApi.questionInfo(http);
        question.setId(questionId);
        return question;
    }

    private List<JsonObject> requestAllPage(String key, PagedApi api, ApiDecoder decoder){
        int page = 0;
        List<JsonObject> result = new ArrayList<>();
        do{
            result.addAll(
                    decoder.decode(
                            requestData(
                                    api.api(key,page))));
        } while(result.size()>0);
        return result;
    }

    private String requestData(String url){
        return proxyHttpClient.getString(url);
    }

    /**
     * 知乎采用分页结构的数据json结构基本一致，统一处理
     * @param jsonStr
     * @return
     */
    public static List<JsonObject> pagedDataDecoder(String jsonStr){
        List<JsonObject> result = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(jsonStr,JsonObject.class);
        JsonObject paging = json.getAsJsonObject("paging");
        JsonArray data = json.getAsJsonArray("data");
        data.forEach(d->result.add(d.getAsJsonObject()));
        if(paging.get("is_end").getAsBoolean()){
            return Collections.emptyList();
        }
        return result;
    }

}

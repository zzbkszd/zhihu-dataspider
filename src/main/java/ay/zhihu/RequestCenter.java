package ay.zhihu;

import ay.common.http.ProxyHttpClient;
import ay.common.http.StringResponseHandler;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Question;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 请求中心
 * Created by 志达 on 2017/4/9.
 */
public class RequestCenter {

    ProxyHttpClient http = new ProxyHttpClient();
    StringResponseHandler stringHandler = new StringResponseHandler();

    public List<JsonObject> getAllFollowees(String userKey) throws IOException {
        return requestAllPage(userKey,RouterCenter::getFolloweesApi,RequestCenter::pagedDataDecoder);
    }

    public List<JsonObject> getAllAnswer(String userKey) throws IOException {
        return requestAllPage(userKey,RouterCenter::getAnswersApi,RequestCenter::pagedDataDecoder);
    }

    public Question getQuestionDetail(int questionId){
        String url = "https://www.zhihu.com/question/"+questionId;
        Question question = QuestionApi.questionInfo(requestData(url));
        question.setId(questionId);
        return question;
    }

    private List<JsonObject> requestAllPage(String key,PagedApi api,ApiDecoder decoder){
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
        HttpResponse response = http.Get(url).setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .setHeader("authorization","Bearer Mi4wQUJCTVdKM0lSUWdBWUFJUWc0UnRDeGNBQUFCaEFsVk5jVm96V1FCSDFPbEo4LXhpVlJ6SG1TU2NkV3QzWlIzY2R3|1494292166|4bcc17a16e272d38e6b1396f2eb24666b75919cb")
                .setHeader("x-udid","AGACEIOEbQuPTsrGA4MGMz5hroc55uog23Q=")
                .execute();
        try {
            return stringHandler.handleResponse(response);
        } catch (IOException e) {
            return null;
        }
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

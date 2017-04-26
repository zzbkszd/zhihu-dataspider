package ay.zhihu.api;

import ay.common.http.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ay.zhihu.RouterCenter.getAnswersApi;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public class Answers {


    public static List<JsonObject> answerForUser(HttpUtil http, String urlToken) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int page = 1;
        while(true){
            String url = getAnswersApi(urlToken,page);
            String jsonStr = http.get(url);
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr,JsonObject.class);
            JsonObject paging = json.getAsJsonObject("paging");
            JsonArray data = json.getAsJsonArray("data");
            data.forEach(d->result.add(d.getAsJsonObject()));
            page++;
            if(paging.get("is_end").getAsBoolean()){
                break;
            }
        }
        return result;
    }

    public static List<JsonObject> answerForQuestion(HttpUtil http, int questionId){
//        https://www.zhihu.com/api/v4/questions/58821326/answers?include=data%5B*%5D.is_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=3&limit=20&sort_by=default
//        limit & offsite
        return null;
    }
}

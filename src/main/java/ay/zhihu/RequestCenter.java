package ay.zhihu;

import ay.http.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ay.zhihu.RouterCenter.*;

/**
 * 请求中心
 * Created by 志达 on 2017/4/9.
 */
public class RequestCenter {

    HttpUtil http = new HttpUtil();

    public List<JsonObject> getAllFollowees(String userKey) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int page = 1;
        int size=1;
        while(size<10){
            String url = getFolloweesApi(userKey,page);
            String jsonStr = http.get(url);
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr,JsonObject.class);
            JsonObject paging = json.getAsJsonObject("paging");
            JsonArray data = json.getAsJsonArray("data");
            data.forEach(d->result.add(d.getAsJsonObject()));
            page++;
            size++;
            if(paging.get("is_end").getAsBoolean()){
                break;
            }
        }

        return result;
    }

    public List<JsonObject> getAllAnswer(String userKey) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int page = 1;
        int size=1;
        while(true){
            String url = getAnswersApi(userKey,page);
            String jsonStr = http.get(url);
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr,JsonObject.class);
            JsonObject paging = json.getAsJsonObject("paging");
            JsonArray data = json.getAsJsonArray("data");
            data.forEach(d->result.add(d.getAsJsonObject()));
            page++;
            size++;
            if(paging.get("is_end").getAsBoolean()){
                break;
            }
        }
//        System.out.println("get all answer in "+page+" pages");

        return result;
    }

    public List<JsonObject> getAllFollowingByHtml(String userKey) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int size=1;
        int page = 1;
        do{
            size=0;
            String url = getFollowingUrl(userKey,page);
            Document html = http.getHtml(url);
            Element dataDiv = html.getElementById("data");
            String jsonStr = dataDiv.attr("data-state");
            jsonStr = StringEscapeUtils.unescapeHtml4(jsonStr);
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
            JsonObject users = json.getAsJsonObject("entities").getAsJsonObject("users");
            Iterator<Map.Entry<String,JsonElement>> iter = users.entrySet().iterator();
            iter.next();
            while(iter.hasNext()){
                Map.Entry<String,JsonElement> entry = iter.next();
                if(entry.getKey().equals(userKey))
                    continue;
                result.add(entry.getValue().getAsJsonObject());
                size++;
            }
            page++;
        }while(size>0);
        return result;
    }

}

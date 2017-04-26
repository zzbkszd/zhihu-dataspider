package ay.zhihu.api;

import ay.common.http.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ay.zhihu.RouterCenter.getFolloweesApi;
import static ay.zhihu.RouterCenter.getFollowingUrl;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public class Followers {

    public static List<JsonObject> followers(HttpUtil http,String urlToken) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int page = 1;
        int size=1;
        while(size<10){
            String url = getFollowingUrl(urlToken,page);
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

    public static List<JsonObject> followees(HttpUtil http,String urlToken) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int page = 1;
        int size=1;
        while(size<10){
            String url = getFolloweesApi(urlToken,page);
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

}

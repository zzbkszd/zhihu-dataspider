package ay.zhihu.api.nologin;

import ay.common.http.HttpUtil;
import com.google.gson.Gson;
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

import static ay.zhihu.RouterCenter.getFollowingUrl;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public class NLFollowers {

    public static List<JsonObject> followers (HttpUtil http,String urlToken) throws IOException {
        List<JsonObject> result = new ArrayList<>();
        int size=1;
        int page = 1;
        do{
            size=0;
            String url = getFollowingUrl(urlToken,page);
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
                if(entry.getKey().equals(urlToken))
                    continue;
                result.add(entry.getValue().getAsJsonObject());
                size++;
            }
            page++;
        }while(size>0);
        return result;
    }

}

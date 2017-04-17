package ay;

import ay.http.HttpUtil;
import ay.jdbc.DBUtils;
import ay.spider.DataCache;
import ay.spider.ZhihuSpider;
import ay.zhihu.RequestCenter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ay.zhihu.RouterCenter.*;


/**
 * Created by Ay on 2017/4/9.
 */
public class Zhihu {



    public static void main(String[] args) throws IOException {
//        DBUtils dbUtils = new DBUtils();
//        try {
//            List<Map<String,Object>> sobj = dbUtils.query("select * from user");
//            for (Map<String, Object> user : sobj) {
//                DataCache.getInstant().lset("user_token_list",user.get("urlToken"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        new ZhihuSpider().work("e-miao-de-nai-ba");
//        test();
//        cleanDB();
    }

    public static void test() throws IOException {
        HttpUtil http = new HttpUtil();

        String url = getFolloweesApi("minmin.gong",1);
        String jsonStr = http.get(url);
        jsonStr = StringEscapeUtils.unescapeHtml4(jsonStr);
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
        System.out.println(json);
    }

    public static void cleanDB() {
        DBUtils dbUtils = new DBUtils();
        try {
            List<Map<String,Object>> questions = dbUtils.query("SELECT count(*) c, questionId ,id from zhihu.question group by questionId order by c desc");
            for (Map<String, Object> question : questions) {
                Long count = (Long) question.get("c");
                int questionId = (int) question.get("questionId");
                int id = (int) question.get("id");

                if(count==1)
                    break;

                dbUtils.update("delete from zhihu.question where questionId=? and not id=?",questionId,id);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

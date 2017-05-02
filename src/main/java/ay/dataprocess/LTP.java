package ay.dataprocess;


import ay.common.http.HttpUtil;
import ay.common.jdbc.DBUtils;
import ay.dataprocess.lingjoin.keyExtractor.CLibraryKeyExtractor;
import ay.dataprocess.lingjoin.nplir.Nlpir;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 志达 on 2017/5/1.
 */
public class LTP {

    static {
        if ( CLibraryKeyExtractor.instance.KeyExtract_Init("", 1, "") ) {
            System.out.println("KeyExtractor初始化成功");
        } else {
            System.out.println("KeyExtractor初始化失败");
            System.exit(-1);
        }
    }

    private static String APIKEY = "y161k2c6h4PopkTXmq1EHf8c1AZZcIHHFK8zpMek";

    public static void main(String[] args) {
        DBUtils dbUtils = DBUtils.getMysqlIns();
        try {
            Map<String,Object> answer = dbUtils.query("select * from answer where id=?",1285600).get(0);
            String content = (String) answer.get("content");
            System.out.println(content);
            System.out.println(word(content));
//            System.out.println(keyExtractor(content));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String word(String text){
        return Nlpir.NLPIR_WordFreqStat(text);
    }

    public static String keyExtractor(String text){
        String keyWordsStr = CLibraryKeyExtractor.instance.KeyExtract_GetKeyWords(text, 10, false);
        CLibraryKeyExtractor.instance.KeyExtract_Exit();
        return keyWordsStr;
    }

    public static JsonArray postQuery(String content){
        HttpUtil httpUtil = new HttpUtil();
        Map<String,String> request = new HashMap<>();
        request.put("api_key",APIKEY);
        request.put("text",content);
        request.put("pattern","ws");
        request.put("format","json");
        String json = httpUtil.post("http://api.ltp-cloud.com/analysis/",request);
        JsonArray jsonObject = new Gson().fromJson(json,JsonArray.class);
        return jsonObject;
    }
}

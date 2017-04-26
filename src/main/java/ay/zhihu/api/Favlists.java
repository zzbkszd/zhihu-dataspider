package ay.zhihu.api;

import ay.common.http.HttpUtil;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public class Favlists {

    //个人收藏夹
    public List<JsonObject> favlists (HttpUtil http,String urlToken){
//        https://www.zhihu.com/api/v4/members/shi-zhi-da-44/favlists?include=data%5B*%5D.updated_time%2Canswer_count%2Cfollower_count%2Ccreator%2Cis_public&offset=0&limit=20
        return null;
    }

    //关注的收藏夹
    public static List<JsonObject> followingFavlists (HttpUtil http,String urlToken){
//        https://www.zhihu.com/api/v4/members/shi-zhi-da-44/following-favlists?include=data%5B*%5D.updated_time%2Canswer_count%2Cfollower_count%2Ccreator&offset=0&limit=20
        return null;
    }

//    https://www.zhihu.com/collection/73905742?page=2 //收藏夹内容
}

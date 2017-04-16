package ay.zhihu;

/**
 * Created by 志达 on 2017/4/9.
 */
public class RouterCenter {

    public static String getAnswersApi(String userKey,int page){
        int pageSize = 20;
        return "https://www.zhihu.com/api/v4/members/"+userKey+"/answers?include=data%5B*%5D.is_normal%2Csuggest_edit%2Ccomment_count%2C" +
                "can_comment%2Ccontent%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%" +
                "2Crelationship.is_authorized%2Cvoting%2Cis_author%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F" +
                "(type%3Dbest_answerer)%5D.topics&offset="+((page-1)*pageSize)+"&limit="+pageSize+"&sort_by=created";
    }

    //他关注的人
    public static String getFolloweesApi(String userKey,int page){
        int pageSize = 20;
        return "https://www.zhihu.com/api/v4/members/"+userKey+"/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2C" +
                "follower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+((page-1)*pageSize)+"&limit="+pageSize+"";
    }

    //关注他的人
    public static String getfollowersApi(String userKey,int page){
        int pageSize = 20;
        return "https://www.zhihu.com/api/v4/members/"+userKey+"/followers?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2C" +
                "follower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=20&limit=20";
    }

    public static String getFollowingUrl(String userKey, int page){
        return "https://www.zhihu.com/people/"+userKey+"/following?page="+page;
    }
    public static String getAnswersUrl(String userKey, int page){
        return "https://www.zhihu.com/people/"+userKey+"/answers?page="+page;
    }
}

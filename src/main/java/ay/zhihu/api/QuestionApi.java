package ay.zhihu.api;

import ay.common.http.HttpUtil;
import ay.common.jdbc.DBUtils;
import ay.zhihu.pojo.Question;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 志达 on 2017/5/8.
 */
public class QuestionApi {

    public static Question questionInfo(String http){

        Question question = new Question();
        Document html = Jsoup.parse(http);
        Elements topicElements = html.select(".QuestionHeader-topics");
        if(topicElements.size()==0)
            return null;
        String topicsStr = topicElements.first().text();
        Element title = html.select(".QuestionHeader-title").first();
        Element desc = html.select(".RichText").first();
        if(desc == null)
            return null;
        question.setTopics(topicsStr);
        question.setTitle(title.text());
        question.setDescription(desc.text());
        Element answerCountStr = html.select(".List-headerText").first();
        if(answerCountStr!=null){
            String ansIntStr = answerCountStr.text().substring(0,answerCountStr.text().indexOf(' '));
            question.setAnswers(Integer.parseInt(ansIntStr));
        }

        Elements border = html.select(".NumberBoard-item");
        for (Element b : border) {
            Elements sub = b.children();
            if(sub.size()==2){
                if(sub.first().text().contains("关注者")){
                    int attentions = Integer.parseInt(sub.last().text());
                    question.setAttention(attentions);
                }else if(sub.first().text().contains("被浏览")){
                    int view = Integer.parseInt(sub.last().text());
                    question.setView(view);
                }
            }
        }

        return question;
    }

    /**
     * 获取问题下的所有答案
     * https://www.zhihu.com/api/v4/questions/23149768/answers?include=data%5B*%5D.is_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=3&limit=20&sort_by=default
     * @param questionId
     * @return
     */
    public static List<JsonObject> getAllAnswer(String questionId){
        return null;
    }

}

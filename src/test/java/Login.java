import ay.common.http.HttpUtil;
import ay.common.http.StaticCookieJar;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by SHIZHIDA on 2017/5/27.
 */
public class Login {

    public static final String loginUrl = "https://www.zhihu.com/login/phone_num";

    public static void main(String[] args) {
//        https://www.zhihu.com/login/phone_num
//        https://www.zhihu.com/captcha.gif?= timestamp
//        _xsrf=ba155b233731879d50ab52e518afedf9&password=ZZbkszd123&captcha_type=cn&phone_num=15011582315
//        post
//        captcha
        HttpUtil httpUtil = new HttpUtil();
        Scanner scanner = new Scanner(System.in);
        try {
            String loginPage = httpUtil.get("https://www.zhihu.com/#signin");
//            <input type="hidden" name="_xsrf" value="131420d9a55b988b887452540679988b"/>
            Document doc = Jsoup.parse(loginPage);
            String xsrf = doc.select("input[name=\"_xsrf\"]").first().attr("value");
//            Element captcha = doc.select(".Captcha-imageConatiner").first();
            System.out.println("https://www.zhihu.com/captcha.gif?="+System.currentTimeMillis());
            String captcha = scanner.next();
            RequestBody loginBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                    "_xsrf="+xsrf+"&password=ZZbkszd123&captcha_type=cn&phone_num=15011582315&captcha_type=");
            Request loginPost = new Request.Builder().url(loginUrl).post(loginBody).build();
            String result = httpUtil.execute(loginPost);
            System.out.println(result);
            StaticCookieJar.printAllCookies();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

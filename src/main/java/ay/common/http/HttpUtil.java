package ay.common.http;

import ay.common.util.CommonConfig;
import ay.common.util.MD5Utils;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 志达 on 2017/4/9.
 */
public class HttpUtil {

    OkHttpClient http = new OkHttpClient();

    public HttpUtil(){
        http = new OkHttpClient.Builder().connectTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .cookieJar(new StaticCookieJar()).build();
    }

    public Document getHtml(String url) throws IOException {
        String body = get(url);
        Document doc = Jsoup.parse(body);
        return doc;
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url)
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .get().build();
        return execute(request);
    }

    public String execute(Request request) throws IOException {
        return http.newCall(request).execute().body().string();
    }

}

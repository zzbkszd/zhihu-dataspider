package ay.common.http;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 志达 on 2017/4/9.
 */
public class HttpUtil {

    OkHttpClient http = new OkHttpClient();

    public Document getHtml(String url) throws IOException {
        String body = get(url);
        Document doc = Jsoup.parse(body);
        return doc;
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url)
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .header("authorization","Bearer Mi4wQUJCTVdKM0lSUWdBWUFJUWc0UnRDeGNBQUFCaEFsVk5jVm96V1FCSDFPbEo4LXhpVlJ6SG1TU2NkV3QzWlIzY2R3|1494292166|4bcc17a16e272d38e6b1396f2eb24666b75919cb")
                .header("x-udid","AGACEIOEbQuPTsrGA4MGMz5hroc55uog23Q=")
                .get().build();
        return http.newCall(request).execute().body().string();
    }

}

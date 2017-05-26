package ay.common.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.Vector;

/**
 * Created by SHIZHIDA on 2017/5/26.
 */
public class StaticCookieJar implements CookieJar {

    static class CookieCache{
        static List<Cookie> cookies = new Vector<>();
        public synchronized static void save(List<Cookie> cookie){
            cookies.addAll(cookie);
        }
        public synchronized static List<Cookie> get(){
            return cookies;
        }
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        CookieCache.save(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return CookieCache.get();
    }
}

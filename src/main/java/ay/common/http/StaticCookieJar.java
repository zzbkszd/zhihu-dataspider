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
        static {
//         =
//            String cookie = "_za=ba8440d5-eca7-4883-ba53-991c767ef084; _ga=GA1.2.1777166177.1455641119; udid=\"ADBAPK3GlQmPTtVCeSuP9uZrCD4r3yo5fq0=|1457537834\"; d_c0=\"AIDAwYgmoQmPTs2KsmVrD0rAtZ-vHJZ5dLU=|1458213451\"; _zap=32ed002a-d8df-45ab-90f3-1b77397111fd; _xsrf=03c362338d7b77fab1fc49f055c45668; q_c1=5beba159b6914da4a0036ed441751310|1495207439000|1453646615000; r_cap_id=\"MDJiYjdmMzU0NDQ2NDczNjkwMmQzM2Q3OTNlMzI3ZDM=|1495724968|9441c6c9927771191e8805f886580d433c9a2231\"; cap_id=\"OWM4YzFlNWM0N2U0NDk3MWFiYzhjODcxODk1YzQ4MzE=|1495724968|6ed0f0f267893b713525d06b58bbde599f3e5995\"; aliyungf_tc=AQAAABUB6nBuAQQAOv75cpm0J6CnsHPX; acw_tc=AQAAAC009W/9ywQAOv75chLMb+5COfpn; capsion_ticket=\"2|1:0|10:1496033172|14:capsion_ticket|44:NWQwYmMwMTdlNDQwNGRmY2FhODgyZjg4OGRiYzdjZTI=|82aec26dccb9c255ee54eca7d82749fc10b913d65d2ffa6f8e7f9ba4950f3bc3\"; z_c0=Mi4wQUJBQVFKSk9fd2tBZ01EQmlDYWhDU1lBQUFCZ0FsVk5tRFJUV1FCdGwyWGlUWnRaTEI2TTM0ZE11VW1lUkpYSlVB|1496041807|a3c3c9ede32d6baca3a6cbc8aefbba695ad80515; __utma=51854390.1777166177.1455641119.1496039311.1496041809.7; __utmb=51854390.0.10.1496041809; __utmc=51854390; __utmz=51854390.1496041809.7.5.utmcsr=baidu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.100--|2=registration_date=20160529=1^3=entry_date=20160124=1";
//            String[] cks = cookie.split(";");
//            for (String ck : cks) {
//                int firstEqual = ck.indexOf('=');
//                String key = ck.substring(0,firstEqual);
//                String v = ck.substring(firstEqual+1);
//                cookies.add(new Cookie.Builder().domain("*.zhihu.com").name(key.trim()).value(v.trim()).path("/").build());
//                System.out.println(key+" = "+v);
//            }
        }
        public synchronized static void save(List<Cookie> cookie){
            cookies.addAll(cookie);
        }
        public synchronized static List<Cookie> get(){
            return cookies;
        }
    }

    public static void printAllCookies(){
        for (Cookie cookie : CookieCache.cookies) {
            System.out.println(cookie.toString());
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

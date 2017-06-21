package ay.common.http;


import ay.common.file.io.FileIO;
import ay.common.http.proxy.ProxyDaemon;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import ay.common.util.CommonConfig;
import ay.spider.SpiderContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 带有代理的httpClient
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyHttpClient {
    OkHttpClient httpClient;
    Log log = LogFactory.getLog(ProxyHttpClient.class);

    ThreadLocal<ProxyInfo> currentProxy = new ThreadLocal<>();


    public ProxyHttpClient (){
        httpClient = new OkHttpClient.Builder()
                .proxySelector(new PoolProxySelector()).connectTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS).cookieJar(new StaticCookieJar())
                .writeTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS).build();
    }

    public String getString(String url){
        Request request = new Request.Builder().url(url)
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .header("Connection","close")
                .get().build();
        try {
            Response response = httpClient.newCall(request).execute();
            String content="";
            if(response.code()>=200 && response.code()<300){
                content = response.body().string();
            }
            response.body().close();
            return content;
        } catch (IOException e) {
            return "";
        } finally {
            currentProxy.get().revert();
        }
    }

    public static void main(String[] args) {
//        https://www.zhihu.com/question/39479153
        SpiderContext context = new SpiderContext();
        ProxyDaemon proxyDaemon = context.enableProxyDeamon();
        while(ProxyPool.size()<5){
            try {
                Thread.sleep(3000);
                System.out.println(proxyDaemon.getWatchedReport());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ProxyHttpClient httpClient = new ProxyHttpClient();
        try {
            FileIO failed = new FileIO("D:\\data\\fail.dat");
            String[] urls= failed.getContent().split("\\n");
            for (String url : urls) {
                System.out.println(url);
                System.out.println(httpClient.getString(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.stop();
    }

    class PoolProxySelector extends ProxySelector {

        @Override
        public List<Proxy> select(URI uri) {
            ProxyInfo proxyInfo = ProxyPool.get();
            currentProxy.set(proxyInfo);
            Proxy proxy = new Proxy(Proxy.Type.HTTP,proxyInfo.address());
            return Arrays.asList(proxy);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            if(ioe.getMessage().contains("Failed to connect"))
                currentProxy.get().setAble(false);
            log.error(ioe.getMessage());
        }
    }

}

package ay.common.http;


import ay.common.http.proxy.ProxyDaemon;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import ay.common.util.CommonConfig;
import ay.spider.SpiderContext;
import ay.zhihu.RequestCenter;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Question;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
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

    ThreadLocal<ProxyInfo> currentProxy = null;


    public ProxyHttpClient (){
        httpClient = new OkHttpClient.Builder()
                .proxySelector(new PoolProxySelector()).connectTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS).build();
    }

    public synchronized String getString(String url){
        Request request = new Request.Builder().url(url)
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36").get().build();
        try {
            String content = httpClient.newCall(request).execute().body().string();
            if(StringUtils.isNotEmpty(content)){
                currentProxy.get().revert();
            }
            return content;
        } catch (IOException e) {
            return "";
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
        for(int i=0;i<100;i++) {
            String web = httpClient.getString("https://www.zhihu.com/question/39479153");
            System.out.println(web);
            Question question = QuestionApi.questionInfo(web);
            System.out.println(question.getTopics());
        }
        context.stop();
    }

    class PoolProxySelector extends ProxySelector {

        @Override
        public List<Proxy> select(URI uri) {
            currentProxy.set( ProxyPool.get());
//                        System.out.println(proxyInfo);
            Proxy proxy = new Proxy(Proxy.Type.HTTP,currentProxy.get().address());
            return Arrays.asList(proxy);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            ProxyPool.remove(currentProxy.get());
            log.error(ioe.getMessage());
        }
    }

}

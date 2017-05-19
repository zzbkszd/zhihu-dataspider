package ay.common.http;

import ay.common.http.executer.HttpGetExecutor;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * 带有代理的httpClient
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyHttpClient {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public static void main(String[] args) throws IOException {
        System.out.println(new StringResponseHandler().handleResponse(new ProxyHttpClient().Get("http://www.baidu.com").execute()));
    }

    public HttpGetExecutor Get(String url){
        ProxyInfo proxyInfo = ProxyPool.get();
        return new HttpGetExecutor(httpclient,url).proxy(proxyInfo);
    }


}

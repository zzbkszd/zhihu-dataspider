package ay.common.http;

import ay.common.http.executer.HttpGetExecutor;
import ay.common.http.executer.HttpPostExecutor;
import ay.common.http.handler.StringResponseHandler;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

/**
 * 带有代理的httpClient
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyHttpClient {

    private CloseableHttpClient httpclient;

    public static void main(String[] args) throws IOException {
        System.out.println(new ProxyHttpClient().Post("http://www.baidu.com").executeForString());
    }

    public ProxyHttpClient(){

        httpclient = HttpClients.createDefault();
    }


    public HttpGetExecutor Get(String url){
        ProxyInfo proxyInfo = ProxyPool.get();
        return new HttpGetExecutor(httpclient,url).proxy(proxyInfo);
    }

    public HttpPostExecutor Post (String url){
        ProxyInfo proxyInfo = ProxyPool.get();
        return new HttpPostExecutor(httpclient,url).proxy(proxyInfo);
    }


}

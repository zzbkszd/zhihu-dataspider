package ay.common.http;

import ay.common.http.executer.HttpGetExecutor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * 带有代理的httpClient
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyHttpClient {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public HttpGetExecutor Get(String url){
        return new HttpGetExecutor(httpclient,url);
    }


}

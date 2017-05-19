package ay.common.http.executer;

import ay.common.http.StringResponseHandler;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpGetExecutor {

    Log LOG = LogFactory.getLog(HttpGetExecutor.class);

    CloseableHttpClient client;

    String url;

    HttpGet getRequest;

    boolean useProxy = false;

    ProxyInfo proxyInfo = null;

    public HttpGetExecutor (CloseableHttpClient client,String url){
        this.client = client;
        this.url = url;
        getRequest = new HttpGet(url);
    }

    public HttpResponse execute() {
        HttpResponse response = null;
       if(useProxy){
           response = tryProxy(1);
       }else{
           try {
               response = client.execute(getRequest);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return response;
    }

    private HttpResponse tryProxy(int times){
        HttpResponse response = null;
        try {
            response = client.execute(getRequest);
            if(response.getStatusLine().getStatusCode()>300){
                LOG.error("catch response code :"+response.getStatusLine().getStatusCode());
            }else{
                return response;
            }
        } catch (IOException e) {
            if(times<100){
                LOG.error("retry proxy "+times+" times");
                this.proxy(ProxyPool.get());
                return tryProxy(times+1);
            }
        }
        return response;
    }

    public HttpGetExecutor proxy(ProxyInfo proxyInfo){
        this.proxyInfo = proxyInfo;
        HttpHost proxy = new HttpHost(proxyInfo.getIp(),proxyInfo.getPort(),"http");
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        getRequest.setConfig(config);
        useProxy = true;
        return this;
    }


    public HttpGetExecutor setHeader(String key,String value){
        getRequest.setHeader(key,value);
        return this;
    }



}

package ay.common.http.executer;

import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxyPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

/**
 * http请求执行
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpExecutor {

    private Log LOG = LogFactory.getLog(HttpExecutor.class);

    protected HttpRequestBase request;

    private CloseableHttpClient client;

    private String url;

    private boolean useProxy = false;
    private boolean retryProxy = true;

    private ProxyInfo proxyInfo = null;

    public HttpExecutor (CloseableHttpClient client,String url){
        this.client = client;
        this.url = url;
    }

    //执行请求
    public HttpResponse execute() {
        HttpResponse response = null;
        if(useProxy){
            response = tryProxy(1);
        }else{
            try {
                response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * 代理可能会失败，当失败时换个代理重试
     * @param times
     * @return
     */
    private HttpResponse tryProxy(int times){
        HttpResponse response = null;
        try {
            response = client.execute(request);
            if(response.getStatusLine().getStatusCode()>300){
                if(retryProxy){
                    LOG.error("request for url:"+url);
                    LOG.error("catch response code :"+response.getStatusLine().getStatusCode()+ " reason phrase : "+response.getStatusLine().getReasonPhrase());
                }
                //5次，允许重复，且不是404
                if(times<5 && retryProxy && response.getStatusLine().getStatusCode()!=404){
                    LOG.error("retry proxy "+times+" times");
                    this.proxy(ProxyPool.get());
                    return tryProxy(times+1);
                }
            }else{
                proxyInfo.setLastConnect(System.currentTimeMillis());
                ProxyPool.add(proxyInfo);
                return response;
            }
        } catch (IOException e) {
            if(times<5 && retryProxy){
                LOG.error("retry proxy "+times+" times");
                this.proxy(ProxyPool.get());
                return tryProxy(times+1);
            }
        }

        return response;
    }

    /**
     * 设置代理
     * @param proxyInfo
     * @param <T>
     * @return
     */
    public <T extends HttpExecutor> T proxy(ProxyInfo proxyInfo){
        this.proxyInfo = proxyInfo;
        HttpHost proxy = new HttpHost(proxyInfo.getIp(),proxyInfo.getPort(),"http");
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        request.setConfig(config);
        useProxy = true;
        return (T) this;
    }


    /**
     * 设置header
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T extends HttpExecutor> T setHeader(String key,String value){
        request.setHeader(key,value);
        return (T) this;
    }

    public boolean isRetryProxy() {
        return retryProxy;
    }

    /**
     * 是否重试代理，若不重试，当前代理失败后则结束请求
     * @param retryProxy
     * @param <T>
     * @return
     */
    public <T extends HttpExecutor> T setRetryProxy(boolean retryProxy) {
        this.retryProxy = retryProxy;
        return (T) this;
    }
}

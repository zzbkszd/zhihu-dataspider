package ay.common.http.executer;

import ay.common.http.StringResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpGetExecutor {

    CloseableHttpClient client;

    String url;

    HttpGet getRequest;

    public HttpGetExecutor (CloseableHttpClient client,String url){
        this.client = client;
        this.url = url;
        getRequest = new HttpGet(url);
    }

    public String execute() throws IOException {
        return client.execute(getRequest, new StringResponseHandler());
    }

    public HttpGetExecutor setHeader(String key,String value){
        getRequest.setHeader(key,value);
        return this;
    }



}

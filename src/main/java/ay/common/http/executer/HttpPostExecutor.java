package ay.common.http.executer;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpPostExecutor {

    CloseableHttpClient client;

    String url;

    HttpPost postRequest;

    public HttpPostExecutor (CloseableHttpClient client,String url){
        this.client = client;
        this.url = url;
        postRequest = new HttpPost(url);
    }

}

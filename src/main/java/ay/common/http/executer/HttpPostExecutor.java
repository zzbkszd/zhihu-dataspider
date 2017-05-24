package ay.common.http.executer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpPostExecutor extends HttpExecutor{

    Log LOG = LogFactory.getLog(HttpPostExecutor.class);

    public HttpPostExecutor (CloseableHttpClient client,String url){
        super(client,url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(8000)
                .setConnectionRequestTimeout(8000)
                .setSocketTimeout(8000).build();
        request = new HttpPost(url);
        request.setConfig(requestConfig);
    }

}

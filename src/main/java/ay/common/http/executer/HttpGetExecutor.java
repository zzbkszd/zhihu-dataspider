package ay.common.http.executer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpGetExecutor extends HttpExecutor{

    Log LOG = LogFactory.getLog(HttpGetExecutor.class);

    public HttpGetExecutor (CloseableHttpClient client,String url){
        super(client,url);
        request = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(8000)
                .setConnectionRequestTimeout(8000)
                .setSocketTimeout(8000).build();
        request.setConfig(requestConfig);
    }

}

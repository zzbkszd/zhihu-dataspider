package ay.common.http.executer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class HttpPostExecutor extends HttpExecutor{

    Log LOG = LogFactory.getLog(HttpPostExecutor.class);

    public HttpPostExecutor (CloseableHttpClient client,String url){
        super(client,url);
        request = new HttpPost(url);
    }

}

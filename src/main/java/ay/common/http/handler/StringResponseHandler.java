package ay.common.http.handler;

import ay.common.util.IOUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文本handler
 * Created by SHIZHIDA on 2017/5/19.
 */
public class StringResponseHandler implements ResponseHandler<String> {

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        if(response==null){
            return null;
        }
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            if(entity==null)
                return null;
            String body = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return body;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }
}

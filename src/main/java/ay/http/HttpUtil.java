package ay.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by 志达 on 2017/4/9.
 */
public class HttpUtil {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public Document getHtml(String url) throws IOException {
        String body = get(url);
        Document doc = Jsoup.parse(body);
        return doc;
    }

    public String get(String url) throws IOException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpGet httpget = new HttpGet(url);

        System.out.println("Executing request " + httpget.getRequestLine());

        httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
//        authorization: oauth c3cef7c66a1843f8b3a9e6a1e3160e20
//        x-udid: AIDAwYgmoQmPTs2KsmVrD0rAtZ-vHJZ5dLU=
        httpget.setHeader("authorization","oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
        httpget.setHeader("x-udid","AIDAwYgmoQmPTs2KsmVrD0rAtZ-vHJZ5dLU=");
//         Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        String responseBody = httpclient.execute(httpget, responseHandler);
        return responseBody;
    }


}

package ay.common.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public String post(String url, Map<String,String> params){

        HttpPost post = new HttpPost(url);
        post.addHeader("charset","utf-8");

        List<NameValuePair> nvpairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            NameValuePair valuePair = new BasicNameValuePair(entry.getKey(),entry.getValue());
            nvpairs.add(valuePair);
        }
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(nvpairs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(entity);

        String response = null;
        try {
            response = httpclient.execute(post,STR_HANDLER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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
        httpget.setHeader("authorization","Bearer Mi4wQUJCTVdKM0lSUWdBWUFJUWc0UnRDeGNBQUFCaEFsVk43c3NMV1FCcXEzXzlDdEhyU0ZTei1fUGpzX3dVaUdsc3dn|1492514216|53cc697d11a503c96451bb21cce8434f6d0f9388");
        httpget.setHeader("x-udid","AGACEIOEbQuPTsrGA4MGMz5hroc55uog23Q=");
//         Create a custom response handler
        String responseBody = httpclient.execute(httpget, STR_HANDLER);
        return responseBody;
    }

    private static ResponseHandler<String> STR_HANDLER;
    static{
        STR_HANDLER = response -> {int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }};
    }



}

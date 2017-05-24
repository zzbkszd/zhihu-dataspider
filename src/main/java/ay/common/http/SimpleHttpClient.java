package ay.common.http;

import ay.common.http.executer.HttpGetExecutor;
import ay.common.http.executer.HttpPostExecutor;
import ay.common.http.handler.ByteResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * 带有代理的httpClient
 * Created by SHIZHIDA on 2017/5/19.
 */
public class SimpleHttpClient {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public SimpleHttpClient(){
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom().
                    loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), (c,a)->true)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

    }

    public static void main(String[] args) throws IOException {
        ByteResponseHandler responseHandler = new ByteResponseHandler();
        SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.Get("https://pic2.zhimg.com/v2-025052a6366ab1a991f17e39484a9b31.png").execute();
        byte[] img = responseHandler.handleResponse(response);
        EntityUtils.consume(response.getEntity());
        System.out.println(img.length);
    }

    public HttpGetExecutor Get(String url){
        return new HttpGetExecutor(httpclient,url);
    }
    public HttpPostExecutor Post (String url){
        return new HttpPostExecutor(httpclient,url);
    }

}

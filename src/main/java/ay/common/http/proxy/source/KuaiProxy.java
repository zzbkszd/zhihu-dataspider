package ay.common.http.proxy.source;

import ay.common.http.HttpUtil;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxySource;

import java.io.IOException;
import java.util.List;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public class KuaiProxy extends ProxySource {
    public static void main(String[] args) {
        new KuaiProxy().getProxy();
    }
    @Override
    public List<ProxyInfo> getProxy() {
        HttpUtil httpClient = new HttpUtil();
        String body = "";
        try {
            body = httpClient.get("http://dev.kuaidaili.com/api/getproxy/?orderid=1029384893932039&num=100&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=1&an_an=1&an_ha=1&sep=1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromText(body);
    }
}

package ay.common.http.proxy.source;

import ay.common.http.HttpUtil;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxySource;
import java.io.IOException;
import java.util.List;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public class XiCiProxy extends ProxySource {
    @Override
    public List<ProxyInfo> getProxy() {
        String ips = null;
        try {
            ips = new HttpUtil().get("http://api.xicidaili.com/free2016.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromText(ips);
    }
}

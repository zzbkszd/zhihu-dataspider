package ay.common.http.proxy.source;

import ay.common.http.SimpleHttpClient;
import ay.common.http.handler.StringResponseHandler;
import ay.common.http.proxy.ProxyDaemon;
import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxySource;
import org.apache.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public class XiCiProxy extends ProxySource {
    @Override
    public List<ProxyInfo> getProxy() {
        List<ProxyInfo> proxies = new ArrayList<>();
        String ips = new SimpleHttpClient().Get("http://api.xicidaili.com/free2016.txt").executeForString();
        return fromText(ips);
    }
}

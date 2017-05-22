package ay.common.http.proxy;

import ay.common.http.proxy.ProxyInfo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public abstract class ProxySource {

    public abstract List<ProxyInfo> getProxy();

    public List<ProxyInfo> fromText(String text){
        List<ProxyInfo> proxies = new ArrayList<>();
        Scanner scanner = new Scanner(new ByteArrayInputStream(text.getBytes()));
        while(scanner.hasNextLine()){
            String[] proxy = scanner.nextLine().split(":");
            ProxyInfo proxyInfo = new ProxyInfo(proxy[0],Integer.parseInt(proxy[1]));
            proxyInfo.setAble(true);
            proxyInfo.setLastUpdate(System.currentTimeMillis());
            proxies.add(proxyInfo);
        }
        return proxies;
    }
}

package ay.common.http.proxy;


import ay.common.http.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.DelayQueue;
import java.util.logging.Logger;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyPool {

    Log LOG = LogFactory.getLog(ProxyPool.class);

    List<ProxyInfo> proxyInfos = new ArrayList<>();

    DelayQueue<ProxyInfo> pool = new DelayQueue<>();

    long lastSync = System.currentTimeMillis();//上次同步时间

    private static class inner {
        public static ProxyPool pool = new ProxyPool();
    }

    public ProxyPool (){}

    public void init(){
        if(proxyInfos.size()<10){
            sync();
            LOG.info("sync "+proxyInfos.size()+" proxys");
            pool.addAll(proxyInfos);
        }
    }

    public void sync(){
        try {
            String ips = new HttpUtil().get("http://api.xicidaili.com/free2016.txt");
            Scanner scanner = new Scanner(new ByteArrayInputStream(ips.getBytes()));
            while(scanner.hasNextLine()){
                String[] proxy = scanner.next().split(":");
                ProxyInfo proxyInfo = new ProxyInfo(proxy[0],Integer.parseInt(proxy[1]));
                proxyInfo.setAble(true);
                proxyInfo.setLastUpdate(System.currentTimeMillis());
                proxyInfos.add(proxyInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProxyInfo get(){
        return inner.pool.getProxy();
    }
    public static void add(ProxyInfo proxyInfo){
        inner.pool.addProxy(proxyInfo);
    }

    public ProxyInfo getProxy(){
        try {
            if(pool.size()==0){
                init();
            }
            return pool.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addProxy(ProxyInfo proxy){
        pool.add(proxy);
    }

    public void remove(ProxyInfo proxyInfo){
        proxyInfos.remove(proxyInfo);
    }

}

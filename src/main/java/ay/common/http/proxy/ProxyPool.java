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

    public static synchronized ProxyInfo get(){
        System.out.println("last proxy count:"+inner.pool.pool.size());
        return inner.pool.getProxy();
    }
    public static synchronized void add(ProxyInfo proxyInfo){
        inner.pool.addProxy(proxyInfo);
    }
    public static synchronized void remove(ProxyInfo proxyInfo){
        inner.pool.removeProxy(proxyInfo);
    }

    public ProxyInfo getProxy(){
        try {
            ProxyInfo info =null;
            do {
                info = pool.take();
            }while(info==null)     ;
            return info;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addProxy(ProxyInfo proxy){
        pool.add(proxy);
    }

    public void removeProxy(ProxyInfo proxyInfo){
        proxyInfos.remove(proxyInfo);
    }

}

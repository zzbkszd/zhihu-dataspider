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
 * 代理池
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyPool {

    static Log LOG = LogFactory.getLog(ProxyPool.class);

    List<ProxyInfo> proxyInfos = new ArrayList<>();

    DelayQueue<ProxyInfo> pool = new DelayQueue<>();

    long lastSync = System.currentTimeMillis();//上次同步时间

    public static void main(String[] args) {
        ProxyInfo info = new ProxyInfo("127.0.0.1",8888);
        ProxyPool.add(info);
        ProxyPool.add(info);
        ProxyPool.get();
        ProxyPool.get();
    }

    public static int size() {
        return inner.pool.pool.size();
    }

    private static class inner {
        public static ProxyPool pool = new ProxyPool();
    }

    public ProxyPool (){}

    /**
     * 获取代理
     * @return
     */
    public static synchronized ProxyInfo get(){
        long start = System.currentTimeMillis();
        System.out.println("last proxy count:"+inner.pool.pool.size());
        ProxyInfo proxyInfo = inner.pool.getProxy();
        long end = System.currentTimeMillis();
        if((end-start)>2000){
            LOG.warn("waiting proxy more then 2 seconds!");
        }
        return proxyInfo;
    }

    /**
     * 添加或返还代理
     * @param proxyInfo
     */
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
        if(!pool.contains(proxy)){
            System.out.println("add proxy");
            pool.add(proxy);
        }
    }

    public void removeProxy(ProxyInfo proxyInfo){
        proxyInfos.remove(proxyInfo);
    }

}

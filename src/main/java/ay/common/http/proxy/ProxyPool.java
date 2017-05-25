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

    boolean proxyLeak = false;
    long release = 0;

    public static int size() {
        return inner.pool.pool.size();
    }

    /**
     * 代理不足
     * @return
     */
    public static boolean isLeak(){
        return inner.pool.proxyLeak;
    }

    /**
     * 取消代理不足
     */
    public static void releaseLeak(){
        inner.pool.proxyLeak = false;
        inner.pool.release = System.currentTimeMillis();
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
//        System.out.println("last proxy count:"+inner.pool.pool.size());
        ProxyInfo proxyInfo = inner.pool.getProxy();
        long end = System.currentTimeMillis();
        if((end-start)>2000){
            LOG.warn("waiting proxy more then 2 seconds!");
            //距离上次释放超过10秒
            if(System.currentTimeMillis()-inner.pool.release>10000)
                inner.pool.proxyLeak = true;
        }else{
            inner.pool.proxyLeak = false;
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
            return null;
        }
    }

    public void addProxy(ProxyInfo proxy){
        if(!pool.contains(proxy)){
            pool.add(proxy);
        }
    }

    public void removeProxy(ProxyInfo proxyInfo){
        proxyInfos.remove(proxyInfo);
    }

}

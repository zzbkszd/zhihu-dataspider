package ay.common.http.proxy;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyPool {

    List<ProxyInfo> proxyInfos = new ArrayList<>();

    DelayQueue<ProxyInfo> pool = new DelayQueue<>();

    long lastSync = System.currentTimeMillis();//上次同步时间

    private static class inner {
        public static ProxyPool pool = new ProxyPool();
    }

    public ProxyPool (){}

    public void init(){

    }

    public void sync(String api){

    }

    public static ProxyInfo get(){
        return inner.pool.getProxy();
    }
    public static void add(ProxyInfo proxyInfo){
        inner.pool.addProxy(proxyInfo);
    }

    public ProxyInfo getProxy(){
        try {
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

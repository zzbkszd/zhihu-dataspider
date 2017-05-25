package ay.common.http.proxy;

import ay.common.http.HttpUtil;
import ay.common.http.proxy.source.XiCiProxy;
import ay.common.util.CommonConfig;
import ay.spider.SpiderContext;
import ay.spider.thread.Dist;
import ay.spider.thread.WatchedThread;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负责更新代理的守护进程
 * Created by SHIZHIDA on 2017/5/22.
 */
public class ProxyDaemon extends WatchedThread<Void,Void> {

    Log LOG = LogFactory.getLog(ProxyDaemon.class);

    //这里需要使用独立的线程池，否则会大量占用资源
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10,1000, TimeUnit.MILLISECONDS,workQueue,
            (r,e)->{if(!e.isShutdown()) {
                try{
                    e.getQueue().put(r);
                } catch (InterruptedException e1) {
                }
            }
            });

    public ProxyDaemon(SpiderContext context){
        super(context, true);
    }

    boolean sleeping = false;
    int count = 0;

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    @Override
    public boolean process(Optional<Void> in, Optional<Dist<Void>> out) {
        try {
            sleeping = false;
            ProxySource src = new XiCiProxy() ;
            List<ProxyInfo> proxys = src.getProxy();
            for (ProxyInfo proxy : proxys) {
                count++;
                executor.execute(new TestThread(proxy,this));
            }
            sleeping = true;
            Thread.sleep(10*60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getWatchedReport() {
        StringBuilder report = new StringBuilder();
        report.append("\t proxy testing:").append(workQueue.size()).append("\n")
                .append("\t total recived proxy count:").append(count).append("\n")
                .append("\t is sleeping :").append(sleeping).append("\n")
                .append("\t success count:").append(successCount).append("\n")
                .append("\t fail count:").append(failCount).append("\n");
        return report.toString();
    }

    /**
     * 测试代理是否可用
     */
    class TestThread implements Runnable{

        ProxyInfo proxyInfo;
        ProxyDaemon proxyDaemon;

        public TestThread(ProxyInfo proxyInfo,ProxyDaemon daemon){
            this.proxyInfo = proxyInfo;
            this.proxyDaemon = daemon;
        }

        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.HTTP,proxyInfo.address()))
                        .connectTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                        .readTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                        .writeTimeout(CommonConfig.getHttpTimeout(), TimeUnit.MILLISECONDS)
                        .build();
                Request request = new Request.Builder().url("http://www.baidu.com").get().build();
                String response = client.newCall(request).execute().body().string();
                if(StringUtils.isNotEmpty(response)){
//                LOG.info("proxy:"+proxyInfo+" test successed!");
                    this.proxyDaemon.successCount.incrementAndGet();
                    ProxyPool.add(proxyInfo);
                }else{
                    this.proxyDaemon.failCount.incrementAndGet();
                }
            } catch (IOException e) {
                this.proxyDaemon.failCount.incrementAndGet();
            }

        }
    }
}

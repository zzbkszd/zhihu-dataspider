package ay.spider;

import ay.common.http.proxy.ProxyDaemon;
import ay.common.http.proxy.ProxyPool;
import ay.spider.thread.ThreadChain;
import ay.spider.thread.WatchedTask;
import ay.spider.thread.WatchedThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.security.provider.ConfigFile;

import java.util.*;
import java.util.concurrent.*;

/**
 * 爬虫容器
 * Created by SHIZHIDA on 2017/5/19.
 */
public class SpiderContext {

    private Log log = LogFactory.getLog(SpiderContext.class);
    private long threadKey = 0;

    List<ThreadChain> chains = new ArrayList<>();

    List<WatchedThread> taskHolder = new Vector<>();
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(6);
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5,6,1000, TimeUnit.MILLISECONDS,workQueue,
            (r,e)->{if(!e.isShutdown()) {
                try{
                    e.getQueue().put(r);
                } catch (InterruptedException e1) {
                }
            }
            });

    public void appendExecutorPoolSize(int size){
        executor.setCorePoolSize(size+executor.getCorePoolSize());
        executor.setMaximumPoolSize(size+executor.getMaximumPoolSize());
    }

    public ThreadChain createChan(){
        ThreadChain chain = new ThreadChain();
        chains.add(chain);
        return chain;
    }

    public void enableWatchReport(){
        executor.execute(new ReportThread(this));
    }
    public ProxyDaemon enableProxyDeamon(){
        ProxyDaemon proxyDaemon = new ProxyDaemon(this);
        executor.execute(proxyDaemon);
        return proxyDaemon;
    }

    public void startUp(){
        for (ThreadChain chain : chains) {
            Iterator<WatchedThread> iter = chain.iterator();
            while(iter.hasNext()){
                iter.next().start();
            }
        }
    }

    public void execTask(Runnable runnable){
        WatchedTask task = new WatchedTask(this,runnable);
        taskHolder.add(task);
        Future f = executor.submit(task);
        task.setExecuteFuture(f);
    }

    public void unregistTask(WatchedTask task){
        taskHolder.remove(task);
    }

    public void stop(){
        for (ThreadChain chain : chains) {
            Iterator<WatchedThread> iter = chain.iterator();
            while(iter.hasNext()){
                iter.next().close();
            }
        }
    }


    public String keyGen(){
        return "[spider thread *"+(threadKey++)+"*]";
    }


    public void flexExecutor() {
        int poolsize = ProxyPool.size();
        int activeTasks = executor.getActiveCount();
        int totalProxy = activeTasks+poolsize;
        //空闲代理数量是执行线程的4倍以上时,或者线程池剩余数量不足
        if(poolsize/activeTasks>=4 || ProxyPool.isLeak()){
            int shouldBe = totalProxy/5;
            shouldBe = (shouldBe>100)?100:shouldBe;
            //重置线程池大小
            executor.setCorePoolSize(shouldBe);
            executor.setMaximumPoolSize((shouldBe*5/4));
            log.info("flex executing pool core size:"+shouldBe+" and max size:"+shouldBe*5/4);
        }
        if(ProxyPool.isLeak()){
            ProxyPool.releaseLeak();
        }
    }
}

package ay.spider;

import ay.spider.thread.ThreadChain;
import ay.spider.thread.WatchedThread;

import java.util.*;
import java.util.concurrent.*;

/**
 * 爬虫容器
 * Created by SHIZHIDA on 2017/5/19.
 */
public class SpiderContext {

    private long threadKey = Double.doubleToLongBits(Math.random()*1000);

    List<ThreadChain> chains = new ArrayList<>();

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(20);
    ThreadPoolExecutor executor = new ThreadPoolExecutor(15,20,1000, TimeUnit.MILLISECONDS,workQueue,
            (r,e)->{if(!e.isShutdown()) {
                try{
                    e.getQueue().put(r);
                } catch (InterruptedException e1) {
                }
            }
            });

    public SpiderContext (){}

    public ThreadChain createChan(){
        ThreadChain chain = new ThreadChain();
        chains.add(chain);
        return chain;
    }

    public void enableWatchReport(){
        execTask(new ReportThread(this));
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
        executor.execute(runnable);
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
        return "*"+(threadKey++ + ++threadKey)+"*";
    }



}

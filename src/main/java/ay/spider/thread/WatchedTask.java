package ay.spider.thread;

import ay.spider.SpiderContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Created by SHIZHIDA on 2017/5/23.
 */
public class WatchedTask extends WatchedThread{

    Log LOG = LogFactory.getLog(WatchedTask.class);

    Runnable task;

    long startTime;
    Future executeFuture;

    Thread localThread;

    public WatchedTask(SpiderContext context,Runnable task) {
        super(context,true);
        this.task = task;
    }

    @Override
    public boolean process(Optional in, Optional out) {
        startTime = System.currentTimeMillis();
        localThread = Thread.currentThread();
        task.run();
        LOG.info("Task "+getKey()+" has finished in thread "+Thread.currentThread().getName());
        getCtx().unregistTask(this);
        localThread = null;
        return false;
    }

    @Override
    public String getWatchedReport() {
        if(startTime==0 || localThread==null){
            return "";
        }

        long costTime=(System.currentTimeMillis()-startTime);

        if(costTime>30000){
//            executeFuture.cancel(true);
//            localThread.interrupt();
        }
        if(costTime>60000){
            LOG.error("undead thread!!! "+localThread.getName()+" state is:"+localThread.getState().name());
            StackTraceElement[] stacks = localThread.getStackTrace();
            for (StackTraceElement stack : stacks) {
                LOG.error("stoping @:"+stack.getClassName()+" method:"+stack.getMethodName()+" line:"+stack.getLineNumber());
            }
        }
//        if(executeFuture.isCancelled()){
//            getCtx().unregistTask(this);
//        }

        return createReport().title("task",getKey())
                .re("thread",localThread.getName()).re("costed time : ",""+costTime)
                .re("thread state:",localThread.getState().name()).toString();
    }

    public void setExecuteFuture(Future executeFuture) {
        this.executeFuture = executeFuture;
    }
}

package ay.spider;

import ay.common.http.proxy.ProxyPool;
import ay.spider.thread.Dist;
import ay.spider.thread.ThreadChain;
import ay.spider.thread.WatchedThread;

import java.util.Iterator;
import java.util.Optional;

/**
 * 监控
 * Created by SHIZHIDA on 2017/5/22.
 */
public class ReportThread extends WatchedThread<Void,Void> {
    public ReportThread(SpiderContext context) {
        super(context, true);
    }

    @Override
    public boolean process(Optional<Void> in, Optional<Dist<Void>> out) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StringBuilder report = new StringBuilder("----------------------------------------------------------------------\n");
        int executingTasks = getCtx().workQueue.size();
        report.append("executing task count :"+executingTasks).append("\n");
        report.append("proxy pool size: "+ ProxyPool.size()).append("\n");
        for (ThreadChain chain : getCtx().chains) {
            Iterator<WatchedThread> iterator = chain.iterator();
            while(iterator.hasNext()){
                WatchedThread thread = iterator.next();
                String watchedReport = thread.getWatchedReport();
                report.append("report for "+thread.getKey()).append("\n");
                report.append(watchedReport).append("\n");
            }
        }
        report.append("----------------------------------------------------------------------\n");
        System.out.println(report.toString());
        return true;
    }
}

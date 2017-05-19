package ay.spider.thread;

import ay.spider.thread.WatchedThread;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 线程链
 */
public class ThreadChain {

    LinkedList<WatchedThread> chain = new LinkedList<>();

    public ThreadChain append(WatchedThread thread){
        if(!chain.isEmpty()){
            chain.getLast().setDist(thread);
        }
        chain.add(thread);
        return this;
    }

    public WatchedThread get(int index){
        return chain.get(index);
    }

    public Iterator<WatchedThread> iterator(){
        return chain.iterator();
    }

}
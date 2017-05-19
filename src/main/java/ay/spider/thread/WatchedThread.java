package ay.spider.thread;

import ay.spider.SpiderContext;
import ay.spider.ZhihuSpiderContext;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public abstract class  WatchedThread<INDATA,OUTDATA> extends Thread implements Dist<INDATA> {

    private SpiderContext ctx;
    private final String key;
    private int runningFlag; //执行标记：0：停止，1：执行，-1：彻底停止
    private LinkedBlockingQueue<INDATA> src;//数据源
    private Dist<OUTDATA> dist;//输出
    private boolean isroot = false;//是否是头结点

    public WatchedThread (SpiderContext context,String key,boolean isroot){
        this(context,key);
        this.isroot = isroot;
    }
    public WatchedThread (SpiderContext context,String key){
        this.ctx = context;
        this.key = key;
        runningFlag = 1;
        src = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while(process(poll(),Optional.ofNullable(dist)) && runningFlag==1){}
        runningFlag = -1;
    }

    public void close(){
        this.runningFlag = 0;
        while(runningFlag!=-1){}
    }

    //推入数据
    public void push(INDATA data){
        try {
            src.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setDist(Dist<OUTDATA> dist){
        this.dist = dist;
    }
    public Optional<INDATA> poll(){
        INDATA indata = null;
        if(!isroot){
            try {
                indata = src.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Optional.ofNullable(indata);
    }






    public abstract boolean process (Optional<INDATA> in,Optional<Dist<OUTDATA>> out);


}

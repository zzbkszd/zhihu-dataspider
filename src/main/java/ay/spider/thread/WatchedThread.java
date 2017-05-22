package ay.spider.thread;

import ay.spider.SpiderContext;

import java.util.Optional;
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

    public WatchedThread (SpiderContext context,boolean isroot){
        this(context);
        this.isroot = isroot;
    }
    public WatchedThread (SpiderContext context){
        this.ctx = context;
        this.key = context.keyGen();
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

    public SpiderContext getCtx() {
        return ctx;
    }

    public void setCtx(SpiderContext ctx) {
        this.ctx = ctx;
    }

    public String getKey() {
        return key;
    }

    public int getRunningFlag() {
        return runningFlag;
    }

    public void setRunningFlag(int runningFlag) {
        this.runningFlag = runningFlag;
    }

    public LinkedBlockingQueue<INDATA> getSrc() {
        return src;
    }

    public void setSrc(LinkedBlockingQueue<INDATA> src) {
        this.src = src;
    }

    public Dist<OUTDATA> getDist() {
        return dist;
    }

    public boolean isIsroot() {
        return isroot;
    }

    public void setIsroot(boolean isroot) {
        this.isroot = isroot;
    }

    public abstract boolean process (Optional<INDATA> in, Optional<Dist<OUTDATA>> out);


}

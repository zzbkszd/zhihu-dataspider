package ay.spider;

/**
 * Created by SHIZHIDA on 2017/4/26.
 */
public abstract class  WatchedThread<INDATA,OUTDATA> extends Thread {

    private ZhihuSpiderContext ctx;

    public WatchedThread (ZhihuSpiderContext context){
        this.ctx = context;
    }

    @Override
    public void run() {
//        super.run();
//        INDATA in = ctx.get...
//        OutBuffer<OUTDATA> out = new OutBuffer<>();
//        while(process(in,out)){
//            INDATA in = ctx.get...
//        }
    }

    public abstract boolean process (INDATA in,OutBuffer<OUTDATA> out);


    private class OutBuffer<OUTDATA>{
//        public void put(OUTDATA out){
//            ctx.put...
//        }
    }

}

package ay.spider;

import ay.spider.thread.Dist;
import ay.spider.thread.WatchedThread;

import java.util.Optional;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ThreadChainTest {

    public static void main(String[] args) {
        SpiderContext context = new SpiderContext();
        context.createChan().append(new WatchedThread<String,String>(context,context.keyGen(),true) {
            @Override
            public boolean process(Optional<String> in, Optional<Dist<String>> out) {
                out.ifPresent(o->{
                    o.push(System.currentTimeMillis()+" from Start ");
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }).append(new WatchedThread<String,String>(context,context.keyGen()) {
            @Override
            public boolean process(Optional<String> in, Optional<Dist<String>> out) {
                if(in.isPresent() && out.isPresent()){
                    out.get().push(in.get()+" append @thread2");
                }
                return true;
            }
        }).append(new WatchedThread<String,String>(context,context.keyGen()) {
            @Override
            public boolean process(Optional<String> in, Optional<Dist<String>> out) {
                System.out.println(in.get()+" output in thread 3");
                return true;
            }
        });
        context.startUp();
    }
}

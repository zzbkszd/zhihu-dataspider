package ay.spider.thread;

import ay.common.http.HttpUtil;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public abstract class SingleRequestThread<T,D> implements Runnable {


    HttpUtil http = new HttpUtil();

    D data;
    Dist<T> dist;
    public SingleRequestThread (D data,Dist<T> dist){
        this.data = data;
        this.dist = dist;
    }

    @Override
    public void run() {
        T result = work(data);
        dist.push(result);
    }

    public abstract <T> T work(D data);

}

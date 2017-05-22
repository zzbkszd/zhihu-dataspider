package ay.common.http.proxy;

import ay.common.http.HttpUtil;
import ay.common.http.SimpleHttpClient;
import ay.common.http.handler.StringResponseHandler;
import ay.spider.SpiderContext;
import ay.spider.thread.Dist;
import ay.spider.thread.WatchedThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

/**
 * 负责更新代理的守护进程
 * Created by SHIZHIDA on 2017/5/22.
 */
public class ProxyDaemon extends WatchedThread<Void,Void> {

    Log LOG = LogFactory.getLog(ProxyDaemon.class);

    public ProxyDaemon(SpiderContext context, boolean isroot) {
        super(context, isroot);
    }

    public ProxyDaemon(SpiderContext context) {
        super(context);
    }

    @Override
    public boolean process(Optional<Void> in, Optional<Dist<Void>> out) {
        try {
            HttpResponse response = new SimpleHttpClient().Get("http://api.xicidaili.com/free2016.txt").execute();
            String ips = new StringResponseHandler().handleResponse(response);
            Scanner scanner = new Scanner(new ByteArrayInputStream(ips.getBytes()));
            while(scanner.hasNextLine()){
                String[] proxy = scanner.next().split(":");
                ProxyInfo proxyInfo = new ProxyInfo(proxy[0],Integer.parseInt(proxy[1]));
                proxyInfo.setAble(true);
                proxyInfo.setLastUpdate(System.currentTimeMillis());
                getCtx().execTask(new TestThread(proxyInfo));
            }
            Thread.sleep(30000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    class TestThread implements Runnable{

        ProxyInfo proxyInfo;

        public TestThread(ProxyInfo proxyInfo){
            this.proxyInfo = proxyInfo;
        }

        @Override
        public void run() {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            HttpResponse response = httpClient.Get("http://www.zhihu.com")
                    .proxy(proxyInfo)
                    .setRetryProxy(false)
                    .execute();
            if(response != null && response.getStatusLine().getStatusCode()<300){
//                LOG.info("proxy:"+proxyInfo+" test successed!");
                System.out.println("proxy:"+proxyInfo+" test successed!");
                ProxyPool.add(proxyInfo);
            }
        }
    }
}

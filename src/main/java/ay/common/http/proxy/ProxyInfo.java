package ay.common.http.proxy;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by SHIZHIDA on 2017/5/19.
 */
public class ProxyInfo implements Delayed {

    String ip;
    int port;
    long lastUpdate;
    long lastConnect;
    boolean able;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastConnect() {
        return lastConnect;
    }

    public void setLastConnect(long lastConnect) {
        this.lastConnect = lastConnect;
    }

    public boolean isAble() {
        return able;
    }

    public void setAble(boolean able) {
        this.able = able;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.toSeconds(5);
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}

package ay.common.http.proxy.source;

import ay.common.http.proxy.ProxyInfo;
import ay.common.http.proxy.ProxySource;
import ay.common.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by SHIZHIDA on 2017/5/22.
 */
public class ProxyFile extends ProxySource {

    public static void main(String[] args) {
        int size = new ProxyFile().getProxy().size();
        System.out.println(size);
    }

    @Override
    public List<ProxyInfo> getProxy() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            IOUtil.copy(ProxyFile.class.getResourceAsStream("/iptable"),byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromText(new String(byteArrayOutputStream.toByteArray()));
    }
}

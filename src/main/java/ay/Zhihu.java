package ay;
import ay.spider.ZhihuSpider;

import java.io.IOException;


/**
 * Created by Ay on 2017/4/9.
 */
public class Zhihu {

    public static void main(String[] args) throws IOException {
        //配置日志
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");// "stdout"为标准输出格式，"debug"为调试模式
        //开始工作
        new ZhihuSpider(true).work("li-gao-fei-68");
    }

}

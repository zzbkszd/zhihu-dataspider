package ay;
import ay.spider.ZhihuSpider;

import java.io.IOException;


/**
 * Created by Ay on 2017/4/9.
 */
public class Zhihu {



    public static void main(String[] args) throws IOException {
        new ZhihuSpider().work("li-gao-fei-68");
    }

}

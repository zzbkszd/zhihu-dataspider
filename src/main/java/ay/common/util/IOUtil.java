package ay.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 志达 on 2017/5/21.
 */
public class IOUtil {

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int len = 0;
        byte[] buf = new byte[1024];
        while((len = inputStream.read(buf))>0){
            outputStream.write(buf,0,len);
        }
    }

}

package ay.common.http.handler;

import ay.common.util.IOUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件下载的handler
 * Created by 志达 on 2017/5/21.
 */
public class ByteResponseHandler implements ResponseHandler<byte[]> {
    @Override
    public byte[] handleResponse(HttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtil.copy(inputStream,outputStream);
        return outputStream.toByteArray();
    }
}

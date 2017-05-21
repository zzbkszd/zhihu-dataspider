package ay.zhihu;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by 志达 on 2017/5/21.
 */
public interface ApiDecoder {

    List<JsonObject> decode (String jsonStr);

}

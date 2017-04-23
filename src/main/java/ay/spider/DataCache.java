package ay.spider;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 志达 on 2017/4/16.
 */
public class DataCache {

    public static final String KEY_ANSWER_DIS = "KEY_ANSWER_DIS";
    public static final String KEY_USER_DIS = "KEY_USER_DIS";
    public static final String KEY_QUESTION_DIS = "KEY_QUESTION_DIS";

    Map<String,Object> cache;

    private static DataCache dataCache;

    public static DataCache getInstant(){
        if(dataCache==null) dataCache = new DataCache();
        return dataCache;
    }

    private DataCache(){
        cache = new ConcurrentHashMap<>();
    }

    public void set(String k,Object v){
        cache.put(k,v);
    }
    public Object get(String k){
        return k;
    }

    public List<Object> lgetAll(String k){
        Object listO = cache.get(k);
        if(listO instanceof List){
            return (List<Object>) listO;
        }
        return null;
    }

    public void lset(String k,Object v){
        List<Object> listO = lgetAll(k);
        if(listO==null){
            listO = new ArrayList<Object>();
            ((List)listO).add(v);
            cache.put(k,listO);
        }
        if(listO instanceof List){
            ((List)listO).add(v);
        }
    }
    public Object lget(String k,int i){
        List<Object> listO = lgetAll(k);
        if(listO==null){
            return null;
        }
        if(listO instanceof List){
            return ((List) listO).get(i);
        }
        return null;
    }
    public boolean lin(String k,Object v){
        List<Object> listO = lgetAll(k);
        if(listO==null){
            return false;
        }
        return listO.contains(v);
    }


}

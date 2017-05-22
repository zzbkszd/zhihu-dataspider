package ay.common.jdbc.pojo;

import java.util.HashMap;

import static ay.common.jdbc.ClassUtil.*;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class Row extends HashMap<String,Object> {

    public <T> T as (Class<T> clazz) throws Exception {

        //直接转为特定类型
        if(this.size()==1){
//            System.out.println("only one");
            Object only = this.values().iterator().next();
            if(only.getClass().equals(clazz)){
                return (T) only;
            }
        }

        //ORM
        T target = getInstance(clazz);
        return into(target);
    }

    public <T> T into (T target) throws Exception {
        if(target == null){
            throw new Exception("Must has default consturctor");
        }
        inject(this,target);
        return target;
    }

}

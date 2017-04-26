package ay.common.jdbc;

import ay.common.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class ClassUtil {

    public static <T> T getInstance (Class<T> clazz){
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if(constructor.getParameterCount()==0){
                    constructor.setAccessible(true);
                    return (T) constructor.newInstance();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object inject(Map<String,Object> src, Object target) throws IllegalAccessException {
        for (String key : src.keySet()) {
            Object value = src.get(key);
            Field handle = getFieldByName(target,key);
            if(handle==null){
                System.out.println("null handle :"+key);
                continue;
            }
            handle.setAccessible(true);
            handle.set(target,value);
        }
        return target;
    }

    public static Field getFieldByName(Object obj, String name){

        String[] names = new String[]{
                StringUtils.uncapitalize(name),
                StringUtils.capitalize(name),
                name,
                StringUtils.uncapitalize(StrUtil.delEnderLine(name))
        };
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            for (String s : names) {
                if(s!=null)
                    if(s.equals(field.getName()))
                        return field;
            }
        }
        return null;
    }

}

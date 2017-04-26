package ay.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class StrUtil {

    /**
     * 下划线名称转为驼峰
     * @param str
     * @return
     */
    public static String delEnderLine(String str){
        if(str.indexOf('_')>0){
            while(str.indexOf('_')>0){
                int index = str.indexOf('_');
                String pre = str.substring(0,index);
                String after = str.substring(index+1,str.length());
                str = pre+ StringUtils.capitalize(after);
            }
            return str;
        }
        return null;
    }

}

package ay.common.file;


import ay.common.file.io.FileIO;
import ay.common.util.IOUtil;

import java.io.*;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class FileUtil {

    public static FileIO getFileIO(String file){
        try {
            return new FileIO(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void copy(File src, File target) {
        try {
            InputStream inputStream = new FileInputStream(src);
            initFile(target);
            OutputStream outputStream = new FileOutputStream(target);
            IOUtil.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initFile(File file) throws Exception {

        if(!file.exists()){
            boolean create;
            if(!file.getParentFile().exists())
                create = file.getParentFile().mkdirs() && file.createNewFile();
            else create = file.createNewFile();
            if(!create){
                throw new Exception("Create File fail!:"+file.getAbsolutePath());
            }
        }
    }

}

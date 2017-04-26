package ay.common.file;

import study.j9.datasource.file.io.FileIO;

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

}

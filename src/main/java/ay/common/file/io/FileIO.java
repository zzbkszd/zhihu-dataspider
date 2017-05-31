package ay.common.file.io;

import ay.common.util.IOUtil;

import java.io.*;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class FileIO {

    File file;

    public FileIO (String path) throws Exception {
        file = new File(path);
        if(file.isDirectory()){
            throw new Exception("FileIO not support a Directory!");
        }
        if(!file.exists()){
            boolean create;
            if(!file.getParentFile().exists())
                create = file.getParentFile().mkdirs() && file.createNewFile();
            else create = file.createNewFile();
            if(!create){
                throw new Exception("Create File fail!:"+path);
            }
        }
    }



    public void copyTo(FileIO target){
        try {
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(target.file);
            IOUtil.copy(inputStream,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public String getContent(){
        return getContent("UTF-8");
    }

    /**
     * 获取所有内容
     * @param charset
     * @return
     */
    public String getContent(String charset){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(file);
            int c = 0;
            byte[] buf = new byte[1024];
            while((c=fis.read(buf))>0){
                baos.write(buf,0,c);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(baos.toByteArray(),charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 追加
     * @param content
     */
    public void append(byte[] content){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复写
     * @param content
     */
    public void write(byte[] content){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}

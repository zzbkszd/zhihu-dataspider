package ay.dataprocess;

import ay.common.file.io.FileIO;
import ay.common.http.handler.DownloadResponseHandler;
import ay.common.http.SimpleHttpClient;
import ay.common.jdbc.DBUtils;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 志达 on 2017/5/18.
 */
public class GetImages {

    public static final String IMG_REG = ".*?\\\"(https://.*?)\\\".*?";




    public static void main(String[] args) throws Exception {
        DBUtils dbUtils = DBUtils.getMysqlIns();
        SimpleHttpClient httpClient = new SimpleHttpClient();
        DownloadResponseHandler downloadResponseHandler = new DownloadResponseHandler();

        List<Map<String,Object>> answers = dbUtils.query("select * from answer limit 150000,50000");
//
//        List<String> good_imgs = new ArrayList<>();

        for (Map<String, Object> answer : answers) {
            String content = (String) answer.get("content");
            int id = (int) answer.get("id");
            int voteup_count = (int) answer.get("voteup_count");
            List<String> images = parseImgs(content);
            if(images.size()>0){
                if(voteup_count/images.size()>100){
                    System.out.printf("catch %d images from answer %d with %d voteups \n",images.size(),id,voteup_count);
                    for (String image : images) {
                        System.out.println("download: "+image);
                        HttpResponse response = httpClient.Get(image).execute();
                        FileIO img = new FileIO("G:\\data\\"+image.substring(image.lastIndexOf('/')));
                        byte[] data = downloadResponseHandler.handleResponse(response);
                        img.write(data);
                    }
                }
            }
        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("<html><head></head><body>");
//        for (String good_img : good_imgs) {
//            builder.append("<img src=\""+good_img+"\"/><br/>");
//        }
//        builder.append("</body></html>");
//        FileIO outhtml = new FileIO("D:\\imgs.html");
//        outhtml.write(builder.toString().getBytes());

    }

    public static List<String> parseImgs(String content){
        List<String> imgs = new ArrayList<>();
        Pattern pattern = Pattern.compile(IMG_REG);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            String url = matcher.group(1);
            url = url.replace("_b","");
            url = url.replace("_r","");
            if((url.endsWith(".jpg") || url.endsWith("png")) && !imgs.contains(url))
                imgs.add(url);
        }
        return imgs;
    }

}

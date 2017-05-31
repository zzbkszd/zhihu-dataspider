import ay.common.file.FileUtil;
import ay.common.file.io.FileIO;
import ay.common.http.HttpUtil;
import ay.common.jdbc.DBUtils;
import ay.common.util.IOUtil;
import ay.dataprocess.lingjoin.nplir.Nlpir;
import ay.zhihu.RequestCenter;
import ay.zhihu.api.QuestionApi;
import ay.zhihu.pojo.Answer;
import ay.zhihu.pojo.Question;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 志达 on 2017/5/29.
 */
public class ScanData {

    public static void main(String[] args) {
        split();
    }

    public static void split(){
        String rootDir = "G:\\data\\zhihu-preprocess\\train\\";
        String testDri = "G:\\data\\zhihu-preprocess\\test\\";

        File train = new File(rootDir);

        for (File topic : train.listFiles()) {
            String dist = testDri+topic.getName();
            File[] answers = topic.listFiles();
            int forTest = answers.length/10;
            for(int i=0;i<forTest;i++){
                File src = answers[i];
                File target = new File(dist+"\\"+src.getName());
                FileUtil.copy(src,target);
                src.delete();
            }
        }


    }

    public static void scan(){
        String rootDir =  "G:\\data\\zhihu-preprocess\\";
        File root = new File(rootDir);
        File[] topics = root.listFiles();
        Map<File,Integer> topicCounter = new TreeMap<>();
        List<Map.Entry<File,Integer>> entries = new ArrayList<>();

        for (File topic : topics) {
            File[] answers = topic.listFiles();
            topicCounter.put(topic,answers.length);
        }

        for (Map.Entry<File, Integer> entry : topicCounter.entrySet()) {
            entries.add(entry);
        }
        entries.sort((a,b)->a.getValue()-b.getValue());
        for (Map.Entry<File, Integer> entry : entries) {
            System.out.println(entry.getKey().getName()+"->"+entry.getValue());
            File src = entry.getKey();
            File dist = new File(src.getParent()+"\\"+entry.getValue()+"-"+src.getName());
            src.renameTo(dist);
        }

    }


    public static void work() {
        String saveDir = "G:\\data\\zhihu\\";
        DBUtils dbUtils = DBUtils.getMysqlIns();
        Map<Integer,String> topicMapper = new HashMap<>();
        Map<String,Integer> topicCounter = new HashMap<>();
        try {
            List<Question> questions = dbUtils.queryOrm("select * from question where topics is not null").to(Question.class);
            System.out.println(questions.size());
            //记录分类
            for (Question question : questions) {
                //分解topic并且保存
                String[] topics = splitTopic(question.getTopics());
                for (String topic : topics) {
                    if(topicCounter.containsKey(topic)){
                        topicCounter.put(topic,topicCounter.get(topic)+1);
                    }else{
                        topicCounter.put(topic,1);
                    }
                }
                //保存问题的topics信息
                topicMapper.put(question.getQuestionId(),question.getTopics());
            }
            List<Integer> qids = new ArrayList<>();
            for (Question question : questions) {
                qids.add(question.getQuestionId());
            }
            String inStr = StringUtils.join(qids,",");
//            System.out.println(inStr);
            List<Answer> answers = dbUtils.queryOrm("select * from answer where questionId in ("+inStr+")").to(Answer.class);
            System.out.println(answers.size());
            int saveCount = 0;
            for (Answer answer : answers) {
                String[] topics = splitTopic(topicMapper.get(answer.getQuestionId()));
                int maxCount = 0;
                String topic = "";
                for (String s : topics) {
                    int c = topicCounter.get(s);
                    if(c>maxCount){
                        maxCount = c;
                        topic = s;
                    }
                }
                FileIO io = FileUtil.getFileIO(saveDir+topic+"\\"+answer.getAnswerId()+".txt");
                Document doc = Jsoup.parse(answer.getContent());
                io.write(doc.text().getBytes());
                saveCount++;
                System.out.println(saveCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static String[] splitTopic(String topics){

        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(topics);
        while(matcher.find()){
            result.add(matcher.group(1));
        }
//        return tps;
        return result.toArray(new String[]{});
    }

    private static String getStackTopic(Stack<Character> stack,boolean encode) {
        StringBuilder builder = new StringBuilder();
        Character[] array = new Character[stack.size()];
        stack.copyInto(array);
        stack.clear();
        for (Character character : array) {
            builder.append(character);
        }
        return builder.toString();
    }

}

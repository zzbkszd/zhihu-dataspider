package ay.dataprocess;

import ay.common.jdbc.DBUtils;
import ay.dataprocess.lingjoin.keyExtractor.CLibraryKeyExtractor;
import ay.dataprocess.lingjoin.nplir.Nlpir;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ay.dataprocess.LTP.keyExtractor;

/**
 * Created by 志达 on 2017/5/8.
 */
public class DataProcess {

    static {
        if ( CLibraryKeyExtractor.instance.KeyExtract_Init("", 1, "") ) {
            System.out.println("KeyExtractor初始化成功");
        } else {
            System.out.println("KeyExtractor初始化失败");
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws SQLException {

        DBUtils dbUtils = DBUtils.getMysqlIns();

        int page = 0;
        List<Map<String,Object>> answers = dbUtils.query("select * from answer limit ?,1000",page*1000);

        while(answers.size()>0){
            List<Object[]> params = new ArrayList<>();
            for (Map<String, Object> answer : answers) {
                String content = (String) answer.get("content");
                int words;
                if(content.length()<30)
                    words = 5;
                else if(content.length()<300)
                    words = 20;
                else words = 30;
                String keyWordsStr = CLibraryKeyExtractor.instance.KeyExtract_GetKeyWords(content, words, false);
                String wordFreqStat = Nlpir.NLPIR_WordFreqStat(content);
                params.add(new Object[]{answer.get("answerId"),keyWordsStr,wordFreqStat,content.length()});
            }
            dbUtils.batchInsert("insert into answer_data(ansid,keywords,wordfreq,length) values (?,?,?,?)",params);
            System.out.println("finish page "+page);
            page++;
            answers = dbUtils.query("select * from answer limit ?,1000",page*1000);
        }

        CLibraryKeyExtractor.instance.KeyExtract_Exit();

    }

}

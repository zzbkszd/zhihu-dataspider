package ay.jdbc;

import com.google.gson.JsonObject;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 志达 on 2017/4/15.
 */
public class DBUtils {

    private BasicDataSource dataSource;

    private QueryRunner runner;

    public DBUtils(){

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://5786f8ea1f83b.bj.cdb.myqcloud.com:17062/zhihu?useUnicode=true&characterEncoding=UTF-8");
        dataSource.setUsername("cdb_outerroot");
        dataSource.setPassword("youquer90AVENUE");
//        dataSource.setUrl("jdbc:mysql://192.168.20.67:3306/score?useUnicode=true&characterEncoding=UTF-8");
//        dataSource.setUsername("root");
//        dataSource.setPassword("f63hiccVEv0mMXi");

        runner = new QueryRunner(dataSource);
    }
    public QueryRunner getRunner(){
        return runner;
    }

    public int insert(String sql,Object... param) throws SQLException {
        return runner.update(sql,param);
    }

    public int update(String sql,Object... param) throws SQLException {
        return runner.update(sql,param);
    }

    public void batchInsert(String sql,List<Object[]> param) throws SQLException {
        System.out.println("batch execute sql :"+sql);
        System.out.println("batch execute size:"+param.size());
        Object[][] array = new Object[param.size()][];
        for(int i=0;i<array.length;i++){
            array[i] = param.get(i);
        }
        runner.insertBatch(sql, new ResultSetHandler<Integer>() {
            @Override
            public Integer handle(ResultSet resultSet) throws SQLException {
                return 1;
            }
        },array);

    }

    public List<Map<String,Object>> query(String sql,Object... param) throws SQLException {
        return runner.query(sql, new RsHandler(),param);
    }

    class RsHandler implements ResultSetHandler<List<Map<String,Object>>> {
        public List<Map<String,Object>> handle(ResultSet resultSet) throws SQLException {
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<Map<String,Object>> rs = new ArrayList<>();
            while(resultSet.next()){
                Map<String,Object> d = new HashMap<>();
                for(int i=0;i<metaData.getColumnCount();i++){
                    String col = metaData.getColumnName(i+1);
                    d.put(col,resultSet.getObject(col));
                }
                rs.add(d);

            }
            return rs;
        }
    }



}

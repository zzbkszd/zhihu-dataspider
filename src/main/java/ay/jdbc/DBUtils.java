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

    /**
     * 当批量插入失败后，逐条插入以减少忽略的损失
     * @param sql
     * @param param
     */
    public void failBatchInsert(String sql,List<Object[]> param){
        for (Object[] objects : param) {
            try {
                insert(sql,objects);
            } catch (SQLException e) {
                System.out.println("insert fail with error:\n"+e.getMessage()+"\n");
            }
        }
    }

    public void batchInsert(String sql,List<Object[]> param) {
        Object[][] array = new Object[param.size()][];
        for(int i=0;i<array.length;i++){
            array[i] = param.get(i);
        }
        try {
            runner.insertBatch(sql, rs ->1 , array);
        } catch (SQLException e) {
            failBatchInsert(sql,param);
        }

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

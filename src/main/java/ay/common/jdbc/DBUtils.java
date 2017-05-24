package ay.common.jdbc;

import ay.common.jdbc.pojo.RowSet;
import static ay.common.util.CommonConfig.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by 志达 on 2017/4/15.
 */
public class DBUtils {

    private BasicDataSource dataSource;

    private QueryRunner runner;

    private static class inner {
        public static DBUtils mysqlIns,hsqlIns;
        static {
            BasicDataSource mysqlSource = new BasicDataSource();
            mysqlSource.setDriverClassName(getMysqlDriver());
            mysqlSource.setUrl(getMysqlUrl());
            mysqlSource.setUsername(getMysqlUser());
            mysqlSource.setPassword(getMysqlPwd());
            mysqlIns = new DBUtils(mysqlSource);

            BasicDataSource hsqlSource = new BasicDataSource();
            hsqlSource.setDriverClassName(getHsqldbDriver());
            hsqlSource.setUrl(getHsqldbUrl());
            hsqlSource.setUsername(getHsqldbUser());
            hsqlSource.setPassword("");
            hsqlIns = new DBUtils(hsqlSource);
            try {
                hsqlIns.update("create table cache_user(uuid varchar(64) not null primary key)");
                hsqlIns.update("create table cache_answer(aid Integer not null primary key)");
                hsqlIns.update("create table cache_question(qid Integer not null primary key)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DBUtils getMysqlIns(){
        return inner.mysqlIns;
    }
    public static DBUtils getHsqlIns(){
        return inner.hsqlIns;
    }

    public DBUtils(BasicDataSource dataSource){
        this.dataSource = dataSource;
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
    public RowSet queryOrm(String sql,Object... param) throws SQLException {
        return runner.query(sql, new OrmHandler(),param);
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
    class OrmHandler implements ResultSetHandler<RowSet> {
        public RowSet handle(ResultSet resultSet) throws SQLException {
            RowSet rs = new RowSet();
            rs.read(resultSet);
            return rs;
        }
    }



}

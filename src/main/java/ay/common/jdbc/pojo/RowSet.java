package ay.common.jdbc.pojo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHIZHIDA on 2017/4/11.
 */
public class RowSet {

    private List<Row> rowSet = new ArrayList<Row>();

    public Row get(int index){
        return rowSet.get(index);
    }

    public void read(ResultSet resultSet) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> colName = new ArrayList<>();
        for(int i=1;i<=metaData.getColumnCount();i++){
            colName.add(metaData.getColumnLabel(i));
        }

        while(resultSet.next()){
            Row row = new Row();
            for (String name : colName) {
                row.put(name,resultSet.getObject(name));
            }
            rowSet.add(row);
        }

    }

    public <T> List<T> to (Class<T> clazz){
        try {
            List<T> result = new ArrayList<>();
            for (Row row : rowSet) {
                result.add(row.as(clazz));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int size() {
        return rowSet.size();
    }
}

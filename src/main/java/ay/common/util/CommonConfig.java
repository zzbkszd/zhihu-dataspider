package ay.common.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Created by SHIZHIDA on 2017/5/24.
 */
public class CommonConfig {

    public static void main(String[] args) {
        System.out.println(getHsqldbDriver());
    }

    public static Configuration configuration;
    static {
        try {
            Configurations conf = new Configurations();
            configuration = conf.properties("config.prop");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getMysqlUrl(){return configuration.getString("common.db.mysql.url");}
    public static String getMysqlUser(){return configuration.getString("common.db.mysql.user");}
    public static String getMysqlDriver(){return configuration.getString("common.db.mysql.driver");}
    public static String getMysqlPwd(){return configuration.getString("common.db.mysql.pwd");}

    public static String getHsqldbUrl(){return configuration.getString("common.db.hsqldb.url");}
    public static String getHsqldbUser(){return configuration.getString("common.db.hsqldb.user");}
    public static String getHsqldbDriver(){return configuration.getString("common.db.hsqldb.driver");}

    public static int getHttpTimeout(){return configuration.getInt("common.http.timeout",5000);}
    public static int getHttpProxyDelay(){return configuration.getInt("common.http.proxy.delay",5);}




}

package com.zhou.crud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhouwenyu
 * @version : 1.0
 * @date : 2019/11/7 15:04
 */
public class Database implements CrudApi {

    private final static Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private static String DRIVER = "com.mysql.jdbc.Driver";
    /** jdbc:mysql://192.168.1.211:3306/auvgo-test?useUnicode=true&characterEncoding=utf8 */
    private static String URL;
    /** root */
    private static String USERNAME;
    /** 123456 */
    private static String PASSWORD;
    /** com.zhou.crud */
    private static String parentPage;
    /** 数据库操作*/
    private static final String SQL = "SELECT * FROM ";

    /*----------------------------------------------API---------------------------------------------------------------*/

    @Override
    public void createJavaCode(String database, String tableName){
        FileCreate fileCreate = new FileCreate();
        if(fileCreate.createPojo(parentPage,database,tableName)){
            LOGGER.info(FileCreate.pojo+" 创建成功");
        }
        if(fileCreate.createDao(parentPage,database,tableName)){
            LOGGER.info(FileCreate.dao+" 创建成功");
        }
        if(fileCreate.createService(parentPage,tableName)){
            LOGGER.info(FileCreate.service+" 创建成功");
        }
    }

    @Override
    public void setDrive(String drive) {
        Database.DRIVER = drive;
    }

    @Override
    public void setUrl(String url) {
        Database.URL = url;
    }

    @Override
    public void setUserAndPwd(String user, String pwd) {
        Database.USERNAME = user;
        Database.PASSWORD = pwd;
    }

    @Override
    public void setParentPage(String parentPage) {
        Database.parentPage = parentPage;
    }
    /*-------------------------------------------数据库操作-----------------------------------------------------------*/

    public Database(String username, String password){
        Database.USERNAME = username;
        Database.PASSWORD = password;
    }
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.info("can not load jdbc driver", e);
        }
    }
    /**
     * 获取数据库连接
     *
     * @return
     */
    private static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     */
    private static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames(String database) {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables("`"+database+"`", null, null, new String[] { "TABLE" });
            while(rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     * @param tableName 表名
     * @return
     */
    public static List<String> getColumnNames(String database, String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + "`"+database+"`.`"+tableName+"`";
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     * @param tableName
     * @return
     */
    public static List<String> getColumnTypes(String database, String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + "`"+database+"`.`"+tableName+"`";
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     * @param tableName
     * @return
     */
    public static List<String> getColumnComments(String database, String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + "`"+database+"`.`"+tableName+"`";
        //列名注释集合
        List<String> columnComments = new ArrayList<>();
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }
}

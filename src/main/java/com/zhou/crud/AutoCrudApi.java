package com.zhou.crud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author : wenyu.chou@outlook.com
 * @version : 1.0
 * @see #getAuthor() <font color = yellow>在此填入你的信息 crtl + 鼠标左键进入</font><br>
 * @see #createPojo(String, String, String,String)  model实体类创建源码
 * @see #createDao(String, String, String,String) mapper创建源码
 * </br>
 */
public class AutoCrudApi {
    private final static Logger log = LoggerFactory.getLogger("AutoCrudApi");
    /**<font color = #e33539>请使用下述main方法生成代码。</font>*/
    public static void main(String[] args) {
        //for example:
        AutoCrudApi api = new AutoCrudApi();
        api.createJavaCode("duojia_4test","s_split_bill","SplitBill");
    }
    final static String DRIVER = "com.mysql.jdbc.Driver";

    /**
     * <font color = yellow>数据库配置，根据实际修改</font><br>jdbc:mysql://192.168.1.211:3306/auvgo-test?useUnicode=true&characterEncoding=utf8
     */
    public String URL = "jdbc:mysql://172.20.101.1:3306/duojia_4test";
    /**
     * root
     */
    public String USERNAME = "duojia4test";
    /**
     * 123456
     */
    public String PASSWORD = "x8QFCFGI6TDuoJia369";

    /**
     * <font color = yellow>生成包地址（根据实际需要修改）for example:com.zhou.crud</font>
     */
    final String parentPage = "com.dogo.pingan";
    public String pojo = "model";
    public String dao = "mapper";
    public String mapperLocation = "/src/main/resources/mapping";

    /**
     * <font color = yellow>生成对应方法名（根据实际需要修改）</font>
     */
    public String sqlInsert = "insertSelective";
    public String sqlDelete = "deleteByPrimaryKey";
    public String sqlUpdate = "updateByPrimaryKeySelective";
    public String sqlSelectList = "selectListByParams";
    public String sqlSelectListByIds = "selectByPrimaryKeys";
    public String sqlSelectObject = "selectByObject";
    public String sqlSelectById = "selectByPrimaryKey";
    /**
     * 数据库操作 (无需修改)
     */
    private static final String SQL = "SELECT * FROM ";

    /**----------------------------------------------API---------------------------------------------------------------*/

    public void createJavaCode(String database, String tableName) {
        this.createJavaCode(database,tableName,null);
    }
    public void createJavaCode(String database, String tableName,String createFileName) {
        if (createPojo(this.parentPage, database, tableName, createFileName)) {
            log.info(pojo + " 创建成功");
        }
        if (createDao(this.parentPage, database, tableName, createFileName)) {
            log.info(dao + " 创建成功");
        }
    }
    /*-------------------------------------------数据库操作-----------------------------------------------------------*/

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            log.info("can not load jdbc driver", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return conn
     */
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            log.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn conn
     */
    private static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     *
     * @param database database
     * @return return
     */
    public List<String> getTableNames(String database) {
        List<String> tableNames = new ArrayList<>();
        Connection conn = this.getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables("`" + database + "`", null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            log.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                log.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param database  database
     * @param tableName table
     * @return return
     */
    public List<String> getColumnNames(String database, String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = this.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + "`" + database + "`.`" + tableName + "`";
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
            log.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    log.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     *
     * @param tableName table
     * @param database  database
     * @return return
     */
    public List<String> getColumnTypes(String database, String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = this.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + "`" + database + "`.`" + tableName + "`";
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
            log.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    log.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     *
     * @param tableName table
     * @param database  database
     * @return return
     */
    public List<String> getColumnComments(String database, String tableName) {
        //与数据库的连接
        Connection conn = this.getConnection();
        PreparedStatement pStemt;
        String tableSql = SQL + "`" + database + "`.`" + tableName + "`";
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
                    log.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }


    /**
     * 创建dao层（orm）和mapper.xml
     *
     * @param parentPage 父包名称
     * @param database   数据库名称
     * @param tableName  数据表名称
     * @return 操作是否成功
     */
    public boolean createDao(String parentPage, String database, String tableName,String createName) {
        String packageName = this.dao;
        File file = new File(System.getProperty("user.dir") + "/src/main/java/" +
                parentPage.replace(".", "/") + "/" + packageName);
        //如果文件夹不存在
        if (!file.exists()) {
            //创建文件夹
            file.mkdir();
        }
        //创建dao层
        List<String> columnNames = this.getColumnNames(database, tableName);
        String createClassName = Optional.ofNullable(createName).orElse(nameChange(tableName, true));
        File dao = new File(file.getAbsoluteFile() + "/" + createClassName + "Mapper.java");
        if (!dao.exists()) {
            String importJar = "import " + parentPage + "." + this.pojo + "." + createClassName + ";" +
                    "\nimport org.apache.ibatis.annotations.Param;" +
                    "\nimport java.util.List;" +
                    "\nimport java.util.Set;" +
                    "\nimport org.apache.ibatis.annotations.Mapper;\n";
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile() + "/" + createClassName + "Mapper.java"));
                //在创建好的文件中写入"具体代码"
                bw.write("package " + parentPage + "." + packageName + ";\n" + importJar + "\n" + getAuthor() + "\n" +
                        "@Mapper\npublic interface " + createClassName + "Mapper{\n" +
                        "    int " + this.sqlInsert + "(" + createClassName + " param);\n" +
                        "    int " + this.sqlDelete + "(Long " + nameChange(columnNames.get(0), false) + ");\n" +
                        "    int " + this.sqlUpdate + "(" + createClassName + " param);\n" +
                        "    List<" + createClassName + "> " + this.sqlSelectList + "(" + createClassName + " param);\n" +
                        "    List<" + createClassName + "> " + this.sqlSelectListByIds + "(@Param(\"ids\")Set<Long> ids);\n" +
                        "    " + createClassName + " " + this.sqlSelectObject + "(" + createClassName + " param);\n" +
                        "    " + createClassName + " " + this.sqlSelectById + "(Long " + nameChange(columnNames.get(0), false) + ");\n" +
                        "\n}");
                //一定要关闭文件
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info(createClassName + "Mapper.java 已经存在(未覆盖，如需重建则先删除此文件)");
        }

        //mapper.xml文件生成
        File mapperPage = new File(System.getProperty("user.dir") + mapperLocation);
        if (!mapperPage.exists() && !mapperPage.mkdir()) {
            //创建文件夹
            return false;
        }
        if (new File(mapperPage.getAbsolutePath() + "/" + createClassName + "Mapper.xml").exists()) {
            log.info(mapperPage.getAbsoluteFile() + "/"
                    + createClassName + "Mapper.xml 已经存在(未覆盖，如需重建则先删除此文件)");
            return false;
        }
        String pojoPath = "\"" + parentPage + "." + this.pojo + "." + createClassName + "\"";
        StringBuilder code = new StringBuilder
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
                        + "\n<mapper namespace=\"");
        code.append(parentPage).append(".").append(this.dao).append(".").append(createClassName).append("Mapper\">\n")
                .append("<resultMap id=\"BaseResultMap\" type=\"").append(parentPage).append(".").append(this.pojo)
                .append(".").append(createClassName).append("\" >");

        StringBuilder sql = new StringBuilder("\n<sql id=\"Base_Column_List\" >\n");

        StringBuilder sqlWhere = new StringBuilder("\n<sql id=\"Example_Where_Clause\"><trim prefix=\"where\" prefixOverrides=\"and|or\">  ");
        StringBuilder insertEnd = new StringBuilder("\n    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
        //增
        StringBuilder insert = new StringBuilder("\n<insert id=\"" + this.sqlInsert + "\" parameterType=").append(pojoPath).append(
                "  useGeneratedKeys=\"true\" keyProperty=\"").append(nameChange(columnNames.get(0), false))
                .append("\" >").append("\n    insert into ").append(tableName).append("\n    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //改
        StringBuilder update = new StringBuilder("\n<update id=\"" + this.sqlUpdate + "\" parameterType=").append(pojoPath).append(" >\nupdate ")
                .append(tableName).append(" <set>");
        for (int i = 0; i < columnNames.size(); i++) {
            String column = columnNames.get(i);
            String name = nameChange(column, false);
            if (i == 0) {
                //主键
                code.append("\n    <id column=\"").append(column).append("\" property=\"").append(name).append("\"/>");
                sql.append(column);
            } else {
                code.append("\n    <result column=\"").append(column).append("\" property=\"").append(name).append("\"/>");
                sql.append(",").append(column);
                insert.append("\n        <if test=\"").append(name).append(" != null\" >\n          ").append(column).append(",\n        </if>");
                insertEnd.append("\n        <if test=\"").append(name).append(" != null\"> \n          #{").append(name).append("},\n        </if>");
                update.append("\n    <if test=\"").append(name).append(" != null\">").append(column).append("=#{")
                        .append(name).append("},</if>");
            }
            sqlWhere.append("\n<if test=\"").append(name).append("!= null\">").append("and ").append(column).append("=#{")
                    .append(name).append("}</if>");
        }
        update.append("</set>\n where ").append(columnNames.get(0)).append("=#{").append(nameChange(columnNames.get(0), false))
                .append("}\n</update>");
        sql.append("</sql>\n");
        sqlWhere.append("</trim></sql>");
        insert.append("\n    </trim>").append(insertEnd).append("\n    </trim>\n</insert>");
        //删
        String delete = "\n<delete id=\"" + this.sqlDelete + "\" parameterType = \"java.lang.Long\">" +
                "\ndelete " +
                "from " + tableName + " where " + columnNames.get(0) + " = " +
                "#{" + nameChange(columnNames.get(0), false) + "}" +
                "\n</delete>";
        //查列表
        String selectList = "\n<select id=\"" + this.sqlSelectList + "\" resultMap=\"BaseResultMap\"  parameterType = " + pojoPath + ">" +
                "\n    select <include refid=\"Base_Column_List\"/> " +
                "\n    from " + tableName + "" +
                "\n    <include refid=\"Example_Where_Clause\"/> " +
                "\n</select>\n";
        //根据id列表查询list
        String selectListByIds = "\n<select id=\"" + this.sqlSelectListByIds + "\" resultMap=\"BaseResultMap\"  parameterType = \"java.lang.Long\">" +
                "\n    select <include refid=\"Base_Column_List\"/> " +
                "\n    from " + tableName + "\n    where " + columnNames.get(0) + " in" +
                "\n    <foreach collection=\"ids\" open=\"(\" close=\")\" separator=\",\" item=\"item\">" +
                "\n        #{item}" +
                "\n    </foreach> " +
                "\n</select>\n";
        //根据id列表查询list
        String selectById = "\n<select id=\"" + this.sqlSelectById + "\" resultMap=\"BaseResultMap\"  parameterType = \"java.lang.Long\">" +
                "\n    select <include refid=\"Base_Column_List\"/> " +
                "\n    from " + tableName + "\n    where " + columnNames.get(0) + " = #{" + nameChange(columnNames.get(0), false) + "}" +
                "\n</select>\n";
        //查单个对象
        String selectObject = "\n<select id=\"" + this.sqlSelectObject + "\" resultMap=\"BaseResultMap\"  parameterType = " + pojoPath + ">" +
                "\n    select <include refid=\"Base_Column_List\"/> " +
                "\n    from " + tableName + "" +
                "\n    <include refid=\"Example_Where_Clause\"/> limit 1" +
                "\n</select>\n<!--以上为自动生成的crud代码可根据具体需求自行修改-->\n";
        code.append("\n</resultMap>").append(sql).append(sqlWhere).append(insert).append(update).append(delete)
                .append(selectList).append(selectListByIds).append(selectById).append(selectObject).append("\n</mapper>");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapperPage.getAbsolutePath() + "/"
                    + createClassName + "Mapper.xml"));
            bw.write(code.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 创建pojo类
     *
     * @param parentPage 父包名称
     * @param database   数据库名称
     * @param tableName  数据表名称
     * @return 操作是否成功
     */
    public boolean createPojo(String parentPage, String database, String tableName,String createName) {
        String packageName = this.pojo;
        File file = new File(System.getProperty("user.dir") + "/src/main/java/" +
                parentPage.replace(".", "/") + "/" + packageName);
        //如果文件夹不存在
        if (!file.exists() && !file.mkdir()) {
            //创建文件夹失败
            return false;
        }
        String createClassName = Optional.ofNullable(createName).orElse(nameChange(tableName, true));
        StringBuilder importJar = new StringBuilder("import java.io.Serializable;\n");
        StringBuilder code = new StringBuilder();
        StringBuilder getterSetter = new StringBuilder();
        List<String> pojoName = this.getColumnNames(database, tableName);
        List<String> pojoType = this.getColumnTypes(database, tableName);
        List<String> pojoDesc = this.getColumnComments(database, tableName);

        for (int i = 0; i < pojoName.size(); i++) {
            String name = nameChange(pojoName.get(i), false);
            String type = processTypeConvert(pojoType.get(i));
            String desc = pojoDesc.get(i) == null || "".equals(pojoDesc.get(i).trim()) ? "请填入描述" : pojoDesc.get(i).trim();
            if ("Date".equals(type) && !importJar.toString().contains("java.util.Date")) {
                importJar.append("\nimport java.util.Date;");
            }
            if("BigDecimal".equals(type) && !importJar.toString().contains("java.math.BigDecimal")){
                importJar.append("\nimport java.math.BigDecimal;");
            }
            code.append("\n    ").append("/**").append(desc).append("*/").append("\n    ").append("private ")
                    .append(type).append(" ").append(name).append(";");
            //编写getter方法
            String str = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
            getterSetter.append("\n    public ").append(type).append(" ").append("get").append(str).append("() {")
                    .append("\n        return ").append(name).append(";\n    }");
            //编写setter方法
            getterSetter.append("\n    public void set").append(str)
                    .append("(").append(type).append(" ").append(name).append(") {\n        this.").append(name)
                    .append(" = ").append(name).append(";\n    }");
        }
        code.append("\n\n").append(getterSetter);
        try {
            //如果文件夹下没有.java就会创建该文件
            File isPojo = new File(file.getAbsoluteFile() + "/"
                    + createClassName + ".java");
            if (isPojo.exists()) {
                log.info(file.getAbsoluteFile() + "/"
                        + createClassName + ".java 已经存在(未覆盖，如需重建则先删除此文件)");
                return true;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile() + "/"
                    + createClassName + ".java"));
            //在创建好的文件中写入"具体代码"
            bw.write("package " + parentPage + "." + packageName + ";\n" + importJar.toString() + "\n" + getAuthor() + "\npublic class " +
                    createClassName + " implements Serializable {\n\n" +
                    "    private static final long serialVersionUID = " + new Random().nextLong() + "L;\n" + code.toString() + "\n}");
            //一定要关闭文件
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * mysql字段类型转java类型
     *
     * @param fieldType mysql字段类型
     * @return 对应的java类型
     */
    public String processTypeConvert(String fieldType) {
        String t = fieldType.toLowerCase();
        if (!t.contains("char") && !t.contains("text")) {
            if (t.contains("bigint")) {
                return "Long";
            } else if (t.contains("int")) {
                return "Integer";
            } else if (!t.contains("date") && !t.contains("time") && !t.contains("year")) {
                if (t.contains("text")) {
                    return "String";
                } else if (t.contains("bit")) {
                    return "Boolean";
                } else if (t.contains("decimal")) {
                    return "BigDecimal";
                } else if (t.contains("float")) {
                    return "Float";
                } else if (t.contains("double")) {
                    return "Double";
                } else {
                    return "String";
                }
            } else {
                return "Date";
            }
        } else {
            return "String";
        }
    }

    /**
     * 将带table_version_id的字符串转换TableVersionId(首字母可选大小写)
     *
     * @param str        待转换的字符
     * @param firstUpper 首字母大写（默认false）
     * @return 转换后的字符
     */
    public static String nameChange(String str, boolean firstUpper) {
        StringBuilder strBuf = new StringBuilder(str.toLowerCase());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strBuf.length(); i++) {
            if (i == 0 && firstUpper && !"_".equals(strBuf.substring(0, 1))) {
                result.append(strBuf.substring(i, i + 1).toUpperCase());
            } else if ("_".equals(strBuf.substring(i, i + 1))) {
                result.append(strBuf.substring(++i, i + 1).toUpperCase());
            } else {
                result.append(strBuf.substring(i, i + 1));
            }
        }
        return result.toString();
    }

    /**
     * 获取签名
     * @see #log <font color = yellow>返回顶部（ctrl+左键）</font>
     * @return str
     */
    public String getAuthor() {
        return "\n\n/**\n * @author wenyu.chou@outlook.com\n * @version 1.0\n * @date "
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "\n *\n */";
    }

}

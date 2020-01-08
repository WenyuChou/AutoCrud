package com.zhou.crud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * @author : zhouwenyu@tom.com
 * @version : 1.0
 */
public class FileCreate {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileCreate.class);
    public static String pojo = "pojo";
    public static String dao = "dao";
    public static String mapper = "mapper";
    public static String mapperLocation = "/src/main/resources/mapper";
    public static String service = "service";
    public static String controller = "controller";

    /**
     * 创建dao层（orm）和mapper.xml
     * @param parentPage  父包名称
     * @param database 数据库名称
     * @param tableName 数据表名称
     * @return 操作是否成功
     */
    public boolean createDao(String parentPage ,String database, String tableName){
        String packageName = FileCreate.dao;
        File file = new File(System.getProperty("user.dir")+"/src/main/java/"+
                parentPage.replace(".", "/")+"/"+packageName);
        //如果文件夹不存在
        if (!file.exists()) {
            //创建文件夹
            file.mkdir();
        }
        //创建dao层
        File dao = new File(file.getAbsoluteFile()+"/"+nameChange(tableName,true)+"Dao.java");
        if(!dao.exists()){
            String importJar = "import "+parentPage+"."+FileCreate.pojo+"."+nameChange(tableName,true)+";" +
                    "\nimport com.zhou.crud.CrudBaseDao;\nimport org.apache.ibatis.annotations.Mapper;\n";
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()+"/"+nameChange(
                        tableName,true)+"Dao.java"));
                //在创建好的文件中写入"具体代码"
                bw.write("package "+parentPage+"."+packageName+";\n"+importJar+"\n" + getAuthor()+"\n" +
                        "@Mapper\npublic interface "+nameChange(tableName,true)+"Dao extends CrudBaseDao<"+
                        nameChange(tableName,true)+"> {\n\n}");
                //一定要关闭文件
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            LOGGER.info(nameChange(tableName,true)+"Dao.java 已经存在(未覆盖，如需重建则先删除此文件)");
        }

        //mapper.xml文件生成
        File mapperPage = new File(System.getProperty("user.dir") + mapperLocation);
        if (!mapperPage.exists()) {
            //创建文件夹
            mapperPage.mkdir();
        }
        if(new File(mapperPage.getAbsolutePath()+"/"+nameChange(tableName,false)+"Mapper.xml").exists()){
            LOGGER.info(mapperPage.getAbsoluteFile()+"/"
                    +nameChange(tableName,true)+ "Mapper.xml 已经存在(未覆盖，如需重建则先删除此文件)");
            return true;
        }
        List<String> pojoName = Database.getColumnNames(database,tableName);
        String pojoPath = "\""+parentPage + "." + FileCreate.pojo + "." + nameChange(tableName,true)+"\"";
        StringBuilder code = new StringBuilder
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
                +"\n<mapper namespace=\"");
        code.append(parentPage).append(".").append(FileCreate.dao).append(".").append(nameChange(tableName,true)).append("Dao\">\n ")
                .append("    <resultMap id=\"BaseResultMap\" type=\"").append(parentPage).append(".").append(FileCreate.pojo)
                .append(".").append(nameChange(tableName,true)).append("\" >");

        StringBuilder sql = new StringBuilder("\n<sql id=\"Base_Column_List\" >\n");

        StringBuilder sqlWhere = new StringBuilder("\n<sql id=\"Example_Where_Clause\"><trim prefix=\"where\" prefixOverrides=\"and|or\">  ");
        StringBuilder insertEnd = new StringBuilder(")\n    values(");
        //增
        StringBuilder insert = new StringBuilder("\n<insert id=\"insert\" parameterType=").append(pojoPath).append(
                "  useGeneratedKeys=\"true\" keyProperty=\"").append(nameChange(pojoName.get(0),false))
                .append("\" >").append("\n    insert into ").append(tableName).append("(");
        //改
        StringBuilder update = new StringBuilder("\n<update id=\"update\" parameterType=").append(pojoPath).append(" >\nupdate ")
                .append(tableName).append( " set <trim  suffixOverrides=\",\" >");
        for (int i = 0; i < pojoName.size(); i++) {
            String column = pojoName.get(i);
            String name = nameChange(column,false);
            code.append("\n    <result column=\"").append(column).append("\" property=\"").append(name).append("\"/>");
            if(i == 0){
                //主键
                sql.append(column);
            }else if( i == 1){
                insert.append(column);
                insertEnd.append("#{").append(name).append("}");
                sql.append(",").append(column);
                update.append("\n    <if test=\"").append(name).append(" != null  \">").append(column).append("=#{")
                        .append(name).append("},</if>");
            }else {
                insert.append(",").append(column);
                insertEnd.append(",#{").append(name).append("}");
                sql.append(",").append(column);
                update.append("\n    <if test=\"").append(name).append(" != null  \">").append(column).append("=#{")
                        .append(name).append("},</if>");
            }
            sqlWhere.append("\n<if test=\"").append(name).append("!= null\">").append("and ").append(column).append("=#{")
                    .append(name).append("}</if>");
        }
        update.append("</trim> where ").append(pojoName.get(0)).append("=#{").append(nameChange(pojoName.get(0),false))
                .append("}\n</update>");
        sql.append("</sql>\n");
        sqlWhere.append("</trim></sql>");
        insert.append(insertEnd).append(")\n</insert>");
        //删
        String delete = "\n<delete id=\"deleteById\" parameterType=" + pojoPath + " >\ndelete " +
                "from " + tableName + " where " + pojoName.get(0) + " = " +
                "#{" + nameChange(pojoName.get(0), false) + "}\n</delete>";
        //查
        String select = "\n<select id=\"selectByModel\" resultMap=\"BaseResultMap\"  parameterType = "+pojoPath+">"+
                "\n    select <include refid=\"Base_Column_List\"/> \n    from "+tableName +"\n    <include refid=\"Example_Where_Clause\"/> "+
                "\n    order by " +pojoName.get(0)+" desc \n</select>\n<!--以上为自动生成的crud代码可根据具体需求自行修改-->\n";
        code.append("\n</resultMap>").append(sql).append(sqlWhere).append(insert).append(update).append(delete)
                .append(select).append("\n</mapper>");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapperPage.getAbsolutePath()+"/"
                    +nameChange(tableName,false)+"Mapper.xml"));
            bw.write(code.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * service层创建
     * @param parentPage 根地址
     * @param tableName 表名
     * @return return
     */
    public boolean createService(String parentPage, String tableName){
        String packageName = FileCreate.service;
        //建立文件夹
        File serviceFile = new File(System.getProperty("user.dir")+"/src/main/java/"+
                parentPage.replace(".", "/")+"/"+packageName);
        File serviceFileImpl = new File(System.getProperty("user.dir")+"/src/main/java/"+
                parentPage.replace(".", "/")+"/"+packageName+"/impl");
        if(!serviceFile.exists()){
            serviceFile.mkdir();
        }
        if(!serviceFileImpl.exists()){
            serviceFileImpl.mkdir();
        }
        //创建BaseService
        File serviceBase = new File(serviceFile.getAbsolutePath()+"/"+nameChange(tableName,true)+"Service.java");
        if(serviceBase.exists()){
            LOGGER.info(nameChange(tableName,true)+"Service.java 已经存在(未覆盖，如需重建则先删除此文件)");
        }else {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(serviceFile.getAbsolutePath()+"/"+nameChange(tableName,true)+"Service.java"));
                //在创建好的文件中写入"具体代码"
                bw.write("package "+parentPage+"."+packageName+";\n\nimport com.zhou.crud.BaseService;\nimport "
                        + parentPage+"."+FileCreate.pojo+"."+nameChange(tableName,true)+
                        ";\n\n" + getAuthor()+"\npublic interface "+nameChange(tableName,true)
                        +"Service extends BaseService<"+nameChange(tableName,true)+"> {\n\n}");
                //一定要关闭文件
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        //创建ServiceImpl
        File serviceImpl = new File(serviceFileImpl.getAbsolutePath()+"/"+nameChange(tableName,true)+"ServiceImpl.java");
        if(serviceImpl.exists()){
            LOGGER.info(nameChange(tableName,true)+"ServiceImpl.java 已经存在(未覆盖，如需重建则先删除此文件)");
        }else {
            try {
                BufferedWriter impl = new BufferedWriter(new FileWriter(serviceFileImpl.getAbsolutePath()+"/"
                        +nameChange(tableName,true)+"ServiceImpl.java"));
                //在创建好的文件中写入"具体代码"
                impl.write("package "+parentPage+"."+packageName+".impl;\n\nimport com.zhou.crud.BaseServiceImpl;\nimport "
                        + parentPage+"."+FileCreate.pojo+"."+nameChange(tableName,true)
                        +";\nimport org.springframework.stereotype.Service;\nimport "+ parentPage+"."+packageName+"."+nameChange(tableName,true)+"Service;"+
                        "\n\n" + getAuthor()+"\n@Service\npublic class "+nameChange(tableName,true)
                        +"ServiceImpl extends BaseServiceImpl<"+nameChange(tableName,true)+"> implements "+nameChange(tableName,true)+"Service {\n\n}");
                //一定要关闭文件
                impl.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    /**
     * 创建pojo类
     * @param parentPage 父包名称
     * @param database  数据库名称
     * @param tableName 数据表名称
     * @return 操作是否成功
     */
    public boolean createPojo(String parentPage ,String database, String tableName){
        String packageName = FileCreate.pojo;
        File file = new File(System.getProperty("user.dir")+"/src/main/java/"+
                parentPage.replace(".", "/")+"/"+packageName);
        //如果文件夹不存在
        if (!file.exists()) {
            //创建文件夹
            file.mkdir();
        }
        StringBuilder importJar = new StringBuilder("import java.io.Serializable;\n");
        StringBuilder code = new StringBuilder();
        StringBuilder getterSetter = new StringBuilder();
        List<String> pojoName = Database.getColumnNames(database,tableName);
        List<String> pojoType = Database.getColumnTypes(database,tableName);
        List<String> pojoDesc = Database.getColumnComments(database,tableName);

        for (int i = 0; i < pojoName.size(); i++) {
            String name = nameChange(pojoName.get(i),false);
            String type = processTypeConvert(pojoType.get(i));
            String desc = pojoDesc.get(i)==null || "".equals(pojoDesc.get(i).trim())?"请填入描述":pojoDesc.get(i).trim();
            if("Date".equals(type) && !importJar.toString().contains("java.util.Date")){
                importJar.append("\nimport java.util.Date;");
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
            File isPojo = new File(file.getAbsoluteFile()+"/"
                    +nameChange(tableName,true)+ ".java");
            if(isPojo.exists()){
                LOGGER.info(file.getAbsoluteFile()+"/"
                        +nameChange(tableName,true)+ ".java 已经存在(未覆盖，如需重建则先删除此文件)");
                return true;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()+"/"
                    +nameChange(tableName,true)+ ".java"));
            //在创建好的文件中写入"具体代码"
            bw.write("package "+parentPage+"."+packageName+";\n"+importJar.toString()+"\n" + getAuthor()+"\npublic class "+
                    nameChange(tableName,true)+" implements Serializable {\n\n" +
                    "    private static final long serialVersionUID = "+new Random().nextLong()+ "L;\n" +code.toString()+"\n}");
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
     * @param str 待转换的字符
     * @param firstUpper 首字母大写（默认false）
     * @return 转换后的字符
     */
    public static String nameChange(String str, boolean firstUpper){
        StringBuilder strBuf = new StringBuilder(str.toLowerCase());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strBuf.length(); i++) {
            if(i==0 && firstUpper && !"_".equals(strBuf.substring(0,1))){
                result.append(strBuf.substring(i,i+1).toUpperCase());
            }else if("_".equals(strBuf.substring(i,i+1))){
                result.append(strBuf.substring(++i,i+1).toUpperCase());
            }else {
                result.append(strBuf.substring(i,i+1));
            }
        }
        return result.toString();
    }
    /**
     * 获取签名
     * @return str
     */
    public String getAuthor(){
        return "\n\n/**\n * @author : zhouwenyu@tom.com\n * @version : 1.0\n * @date : "
                +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())+"\n*\n */";
    }
}

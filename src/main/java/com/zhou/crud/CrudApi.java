package com.zhou.crud;

/**
 * @author : zhouwenyu
 * @version : 1.0
 * @Project : AutoCrud
 * @date : 2019/11/7 16:09
 */
public interface CrudApi {
    /**
     * 创建代码
     * @param database 数据库名称
     * @param tableName 表
     */
    void createJavaCode(String database, String tableName);

    /**
     * 设置数据库驱动 默认：
     * @param drive 数据库驱动
     */
    void setDrive(String drive);

    /**
     * 设置数据库连接地址
     * @param url 数据库连接地址
     */
    void setUrl(String url);

    /**
     * 设置数据库用户名和密码
     * @param user 用户名
     * @param pwd 密码
     */
    void setUserAndPwd(String user,String pwd);

    /**
     * 这是父类包名地址（在哪个包下生成）
     * @param parentPage 父类包名地址
     */
    void setParentPage(String parentPage);
}

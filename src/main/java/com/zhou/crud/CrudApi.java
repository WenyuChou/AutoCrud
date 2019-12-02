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
}

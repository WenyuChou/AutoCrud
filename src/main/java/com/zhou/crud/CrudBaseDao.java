package com.zhou.crud;

import java.util.List;


/**
 * @author : zhouwenyu@tom.com
 * @version : 1.0
 */
public interface CrudBaseDao<T> {
    /**
     * insert
     * @param var1 var1
     * @return return
     */
    int insert(T var1);

    /**
     * update
     * @param var1 var1
     * @return return
     */
    int update(T var1);

    /**
     * select
     * @param var1 var1
     * @return return
     */
    List<T> selectByModel(T var1);

    /**
     * delete
     * @param var1 var1
     * @return return
     */
    Integer deleteById(T var1);

}
package com.zhou.crud;

import java.util.List;


/**
 * @author : Wenyu Zhou
 * @version : 1.0
 * @email : m13718591023@163.com
 * @date  : 2019-11-07 17:43:24
 */
public interface CrudBaseDao<T> {

    int insert(T var1);
    /**只修改传参部分*/
    int update(T var1);

    List<T> selectByModel(T var1);

    Integer deleteById(T var1);

}
package com.zhou.crud;


import java.util.List;


/**
 * @author : Wenyu Zhou
 * @version : 1.0
 * 2019-11-22 16:30:30
 */
public interface BaseService<T> {
    Integer insert(T model);
    /**只修改传参部分*/
    Integer update(T model);

    List<T> selectByModel(T model);

    Integer deleteById(T model);

}
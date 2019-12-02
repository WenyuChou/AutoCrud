package com.zhou.crud;


import java.util.List;


/**
 * @author : Wenyu Zhou
 * @version : 1.0
 * 2019-11-22 16:30:30
 */
public interface BaseService<T> {
    Integer insert(T model);

    Integer update(T model);

    PageInfo<T> selectPage(T model);

    List<T> selectByModel(T model);

    Integer deleteById(T model);

}
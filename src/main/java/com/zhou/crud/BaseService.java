package com.zhou.crud;


import java.util.List;


/**
 * @author : zhouwenyu@tom.com
 * @version : 1.0
 */
public interface BaseService<T> {
    /**
     * 新增
     * @param model model
     * @return return
     */
    Integer insert(T model);

    /**
     * 只修改传参部分
     * @param model model
     * @return return
     */
    Integer update(T model);

    /**
     * select
     * @param model model
     * @return return
     */
    List<T> selectByModel(T model);

    /**
     * delete
     * @param model model
     * @return return
     */
    Integer deleteById(T model);

}
package com.zhou.crud;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : zhouwenyu@tom.com
 * @version : 1.0
 */
public class BaseServiceImpl<T> implements BaseService<T> {

    @Resource
    private CrudBaseDao<T> baseDao;

    @Override
    public Integer insert(T model) {
        return baseDao.insert(model);
    }

    @Override
    public Integer update(T model) {
        return baseDao.update(model);
    }

    @Override
    public List<T> selectByModel(T model) {
        return baseDao.selectByModel(model);
    }

    @Override
    public Integer deleteById(T model) {
        return baseDao.deleteById(model);
    }
}

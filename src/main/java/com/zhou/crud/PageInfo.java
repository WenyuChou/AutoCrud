package com.zhou.crud;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author : m13718591023@163.com zhouwenyu
 * @version : 1.0
 * @Package : com.zhou.crud
 * @Project : AutoCrud
 * @date : 2019/11/22 16:33
 */
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = -5068681224447345474L;
    private boolean hasNext;
    private boolean hasPre;
    private List<T> list;
    private int index = 1;
    private int size = 15;
    private int totalPage;
    private int total;

    public PageInfo(int index, int size) {
        this.index = index;
        this.size = size;
    }

    public PageInfo(int index) {
        this.index = index;
    }

    public PageInfo() {
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isHasPre() {
        return this.hasPre;
    }

    public void setHasPre(boolean hasPre) {
        this.hasPre = hasPre;
    }

    public boolean isHasNext() {
        return this.hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getList() {
        return this.list == null ? Collections.emptyList() : this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.setTotalPage(total % this.size == 0 ? total / this.size : total / this.size + 1);
        if (this.index > 1) {
            this.setHasPre(true);
        } else {
            this.setHasPre(false);
        }

        if (this.index < this.totalPage) {
            this.setHasNext(true);
        } else {
            this.setHasNext(false);
        }

    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Page [当前页数=" + this.index + ", 总页数=" + this.totalPage + ", 总条数=" + this.total + ", 大小=" + this.size + "]";
    }
}

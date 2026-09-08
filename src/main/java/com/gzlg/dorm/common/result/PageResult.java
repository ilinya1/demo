package com.gzlg.dorm.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果体。
 * 契约对齐前端 { list, total }（前端分页读 res.list / res.total）。
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数据 */
    private List<T> list;
    /** 总记录数 */
    private long total;

    public PageResult() {
    }

    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public static <T> PageResult<T> of(List<T> list, long total) {
        return new PageResult<>(list, total);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
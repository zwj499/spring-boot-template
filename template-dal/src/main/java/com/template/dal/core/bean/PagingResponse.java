package com.template.dal.core.bean;

import java.util.List;

public class PagingResponse<T> extends ApiBaseResponse<PagingResponse<T>> {
    private List<T> rows;
    private long total;

    public PagingResponse() {
    }

    public PagingResponse(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public List<T> getRows() {
        return this.rows;
    }

    public long getTotal() {
        return this.total;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String toString() {
        return "PagingResponse(rows=" + this.getRows() + ", total=" + this.getTotal() + ")";
    }
}
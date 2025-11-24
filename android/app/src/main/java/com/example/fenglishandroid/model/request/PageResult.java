package com.example.fenglishandroid.model.request;

import java.util.ArrayList;
import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pages;
    private int page;
    private int size;

    public PageResult(List<T> list, long total, int pages, int page, int size) {
        this.list = list != null ? list : new ArrayList<>(); // 确保list不为null
        this.total = total;
        this.pages = pages;
        this.page = page;
        this.size = size;
    }

    // getter和setter
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
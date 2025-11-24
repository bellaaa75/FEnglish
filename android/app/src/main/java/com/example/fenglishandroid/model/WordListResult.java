package com.example.fenglishandroid.model;

import java.util.List;

public class WordListResult {
    private boolean success;
    private String message; // 用于错误信息
    private List<WordSimpleResp> data; // 单词数组
    private int total; // 总条数
    private int totalPages; // 总页数
    private int currentPage;
    private int pageSize;

    // 生成所有字段的getter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<WordSimpleResp> getData() { return data; }
    public void setData(List<WordSimpleResp> data) { this.data = data; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
package com.example.fenglishandroid.model;

import com.example.fenglishandroid.model.request.User;

import java.util.List;

public class UserListResponse {
    private boolean success;
    private int totalPages;
    private long totalCount;
    private int currentPage;
    private List<User> users;

    public UserListResponse() {}

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}
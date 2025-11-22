package com.example.fenglishandroid.repository;

import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.model.request.VocabularyBookAddReq;
import com.example.fenglishandroid.model.request.VocabularyBookUpdateReq;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyBookRepository {
    private VocabularyBookService service;

    public VocabularyBookRepository() {
        this.service = RetrofitClient.getVocabularyBookService();
    }

    /**
     * 获取单词书列表（空关键词查询全部）
     */
    public void getBookList(int page, int size, final RepositoryCallback<PageResult<VocabularyBookSimpleResp>> callback) {
        service.searchBooks("", page, size).enqueue(new Callback<Result<PageResult<VocabularyBookSimpleResp>>>() {
            @Override
            public void onResponse(Call<Result<PageResult<VocabularyBookSimpleResp>>> call,
                                   Response<Result<PageResult<VocabularyBookSimpleResp>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<Result<PageResult<VocabularyBookSimpleResp>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * 搜索单词书（带关键词）
     */
    public void searchBooks(String keyword, int page, int size, final RepositoryCallback<PageResult<VocabularyBookSimpleResp>> callback) {
        service.searchBooks(keyword, page, size).enqueue(new Callback<Result<PageResult<VocabularyBookSimpleResp>>>() {
            @Override
            public void onResponse(Call<Result<PageResult<VocabularyBookSimpleResp>>> call,
                                   Response<Result<PageResult<VocabularyBookSimpleResp>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<Result<PageResult<VocabularyBookSimpleResp>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * 删除单词书
     */
    public void deleteBook(String bookId, final RepositoryCallback<Void> callback) {
        service.deleteBook(bookId).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("删除失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //新增单词书
    public void addBook(VocabularyBookAddReq req, final RepositoryCallback<Void> callback) {
        service.addBook(req).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("新增失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //更新单词书
    public void updateBook(String bookId, VocabularyBookUpdateReq req, final RepositoryCallback<Void> callback) {
        service.updateBook(bookId, req).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("更新失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //获取单词书详情
    public void getBookDetail(String bookId, final RepositoryCallback<VocabularyBookDetailResp> callback) {
        service.getBookDetail(bookId).enqueue(new Callback<Result<VocabularyBookDetailResp>>() {
            @Override
            public void onResponse(Call<Result<VocabularyBookDetailResp>> call,
                                   Response<Result<VocabularyBookDetailResp>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onFailure(new Exception("获取详情失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<VocabularyBookDetailResp>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * 向单词书添加单词
    public void addWordToBook(String bookId, String wordId, final RepositoryCallback<Void> callback) {
        service.addWordToBook(bookId, wordId).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("添加单词失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
     */

    //从单词书删除单词
    public void removeWordFromBook(String bookId, String wordId, final RepositoryCallback<Void> callback) {
        service.removeWordFromBook(bookId, wordId).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("删除单词失败：" + (response.body() != null ? response.body().getMessage() : "未知错误")));
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    /**
     * 通用响应处理方法
     */
    private <T> void handleResponse(Response<Result<T>> response, RepositoryCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            if (response.body().isSuccess()) {
                callback.onSuccess(response.body().getData());
            } else {
                callback.onFailure(new Exception(response.body().getMessage()));
            }
        } else {
            callback.onFailure(new Exception("网络请求失败，状态码：" + response.code()));
        }
    }

    /**
     * 回调接口
     */
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onFailure(Throwable throwable);
    }
}
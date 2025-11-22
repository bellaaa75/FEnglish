package com.example.fenglishandroid.repository;

import android.content.Context;
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
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

    // 获取单词书列表
    public void getBookList(int page, int size, final RepositoryCallback<PageResult<VocabularyBookSimpleResp>> callback) {
        service.getBookList("", page, size).enqueue(new Callback<Result<PageResult<VocabularyBookSimpleResp>>>() {
            @Override
            public void onResponse(Call<Result<PageResult<VocabularyBookSimpleResp>>> call,
                                   Response<Result<PageResult<VocabularyBookSimpleResp>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onFailure(new Exception("获取数据失败"));
                }
            }

            @Override
            public void onFailure(Call<Result<PageResult<VocabularyBookSimpleResp>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // 其他方法类似实现：searchBooks、deleteBook、addBook等

    // 回调接口
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onFailure(Throwable throwable);
    }
}
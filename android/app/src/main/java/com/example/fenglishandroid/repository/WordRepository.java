package com.example.fenglishandroid.repository;

import androidx.annotation.NonNull;

import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordRepository {
    private final VocabularyBookService vocabularyBookService;

    public WordRepository() {
        // 复用单词书服务接口，而非WordService
        this.vocabularyBookService = RetrofitClient.getVocabularyBookService();
    }

    /**
     * 通过单词书详情接口获取单词列表并进行客户端分页
     * 适配思路：复用获取单词书详情的接口，从详情中提取单词列表后手动分页
     */
    public void getWordsByBookId(String bookId, int page, int size, final RepositoryCallback<PageResult<WordSimpleResp>> callback) {
        vocabularyBookService.getBookDetail(bookId).enqueue(new Callback<Result<VocabularyBookDetailResp>>() {
            @Override
            public void onResponse(Call<Result<VocabularyBookDetailResp>> call, Response<Result<VocabularyBookDetailResp>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        VocabularyBookDetailResp detailResp = response.body().getData();
                        if (detailResp == null) {
                            callback.onFailure(new Exception("单词书详情为空"));
                            return;
                        }

                        List<WordSimpleResp> allWords = detailResp.getWordList();
                        if (allWords == null) {
                            allWords = new ArrayList<>();
                        }

                        // 客户端分页处理
                        int total = allWords.size();
                        int totalPages = (total + size - 1) / size; // 计算总页数
                        int fromIndex = (page - 1) * size;
                        int toIndex = Math.min(fromIndex + size, total);

                        List<WordSimpleResp> pageWords;
                        if (fromIndex >= total) {
                            pageWords = new ArrayList<>();
                        } else {
                            pageWords = allWords.subList(fromIndex, toIndex);
                        }

                        // 构建分页结果
                        PageResult<WordSimpleResp> pageResult = new PageResult<>(
                                pageWords,
                                total,
                                totalPages,
                                page,
                                size
                        );
                        callback.onSuccess(pageResult);
                    } else {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "获取单词失败";
                        callback.onFailure(new Exception(errorMsg));
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Call<Result<VocabularyBookDetailResp>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // 复用项目中已有的回调接口设计
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onFailure(Throwable throwable);

        void onFailure(@NonNull Call<Result<PageResult<WordSimpleResp>>> call, @NonNull Throwable t);
    }
}
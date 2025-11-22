package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.request.VocabularyBookAddReq;
import com.example.fenglishandroid.model.request.VocabularyBookUpdateReq;

import retrofit2.Call;
import retrofit2.http.*;

public interface VocabularyBookService {

    // 搜索单词书
    @GET("api/vocabulary-books/search")
    Call<Result<PageResult<VocabularyBookSimpleResp>>> searchBooks(
            @Query("name") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    // 删除单词书
    @DELETE("api/vocabulary-books/{bookId}")
    Call<Result<Void>> deleteBook(@Path("bookId") String bookId);

    // 新增单词书
    @POST("api/vocabulary-books")
    Call<Result<Void>> addBook(@Body VocabularyBookAddReq bookData);

    // 获取单词书详情
    @GET("api/vocabulary-books/{bookId}")
    Call<Result<VocabularyBookDetailResp>> getBookDetail(@Path("bookId") String bookId);

    // 更新单词书
    @PUT("api/vocabulary-books/{bookId}")
    Call<Result<Void>> updateBook(
            @Path("bookId") String bookId,
            @Body VocabularyBookUpdateReq bookData
    );

    // 向单词书添加单词
    @POST("api/vocabulary-books/{bookId}/words")
    Call<Result<Void>> addWordToBook(
            @Path("bookId") String bookId,
            @Body WordAddReq payload
    );

    // 从单词书删除单词
    @DELETE("api/vocabulary-books/{bookId}/words/{wordId}")
    Call<Result<Void>> removeWordFromBook(
            @Path("bookId") String bookId,
            @Path("wordId") String wordId
    );

    // 请求参数类
    class WordAddReq {
        private String wordId;

        public String getWordId() { return wordId; }
        public void setWordId(String wordId) { this.wordId = wordId; }
    }
}
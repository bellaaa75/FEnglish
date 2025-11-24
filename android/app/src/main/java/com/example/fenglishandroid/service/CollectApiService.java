package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.CollectWordDTO;
import com.example.fenglishandroid.model.CollectBookDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface CollectApiService {

    /* 收藏单词 */
    @POST("api/collect/word/{wordId}")
    Call<BaseResponse<Void>> collectWord(@Path("wordId") String wordId);

    /* 取消收藏单词 */
    @DELETE("api/collect/word/{wordId}")
    Call<BaseResponse<Void>> unCollectWord(@Path("wordId") String wordId);

    /* 收藏单词书 */
    @POST("api/collect/book/{bookId}")
    Call<BaseResponse<Void>> collectBook(@Path("bookId") String bookId);

    /* 取消收藏单词书 */
    @DELETE("api/collect/book/{bookId}")
    Call<BaseResponse<Void>> unCollectBook(@Path("bookId") String bookId);

    /* 我的单词收藏 */
    @GET("api/collect/words")
    Call<BaseResponse<Map<String, Object>>> myWordCollects(
            @Query("page") int page,
            @Query("size") int size);

    /* 我的单词书收藏 */
    @GET("api/collect/books")
    Call<BaseResponse<Map<String, Object>>> myBookCollects(
            @Query("page") int page,
            @Query("size") int size);
}
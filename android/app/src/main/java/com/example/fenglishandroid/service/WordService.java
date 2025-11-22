package com.example.fenglishandroid.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// 公共接口，与后端接口完全匹配
public interface WordService {
    /**
     * 获取单词列表（后端是GET请求，参数通过Query传递）
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param keyword 搜索关键词（可选）
     * @return 响应数据
     */
    @GET("api/words")
    Call<ResponseBody> getWordList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query(value = "keyword", encoded = true) String keyword // encoded=true处理中文关键词
    );

    /**
     * 删除单词（后端是DELETE请求）
     * @param wordId 单词Id
     * @return 响应数据
     */
    @DELETE("api/words/{wordId}")
    Call<ResponseBody> deleteWord(@Path("wordId") String wordId); // 后端要求字符串类型

    // 模糊搜索单词接口（匹配后端URL）
    @GET("api/words/name/fuzzy/{wordName}")
    Call<ResponseBody> searchWordByFuzzyName(@Path("wordName") String wordName);
}
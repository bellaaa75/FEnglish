package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.LearningStateResp;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.StudyRecordRequest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @PUT("api/words/{wordId}")
    Call<ResponseBody> updateWord(
            @Path("wordId") String wordId,
            @Body RequestBody requestBody
    );

    @POST("api/words")
    Call<ResponseBody> addWord(@Body RequestBody requestBody);

    @GET("api/words/{wordId}")
    Call<WordSimpleResp> getWordDetail(@Path("wordId") String wordId);


    // ====================== 新增：学习状态相关接口（仅新增，不影响原有）======================
    /**
     * 查询用户对某个单词的学习状态
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return BaseResponse<LearningStateResp> 学习状态信息（使用你已有的BaseResponse）
     */
    @GET("api/learning-states")
    Call<BaseResponse<LearningStateResp>> getLearningState(
            @Query("userId") String userId,
            @Query("wordId") String wordId
    );

    /**
     * 创建学习状态（未查询到时调用，默认标记为"已学"）
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return BaseResponse<Boolean> 是否创建成功（使用你已有的BaseResponse）
     */
    @POST("api/learning-states")
    Call<BaseResponse<Boolean>> createLearningState(
            @Query("userId") String userId,
            @Query("wordId") String wordId
    );

    /**
     * 修改学习状态（查询到"未学"时调用，改为"已学"）
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param state 目标状态（固定传"已学"）
     * @return BaseResponse<Boolean> 是否修改成功（使用你已有的BaseResponse）
     */
    @PUT("api/learning-states")
    Call<BaseResponse<Boolean>> updateLearningState(
            @Query("userId") String userId,
            @Query("wordId") String wordId,
            @Query("state") String state
    );

    @POST("api/study-records")
    Call<BaseResponse<Boolean>> addStudyRecord(@Body StudyRecordRequest request);
}

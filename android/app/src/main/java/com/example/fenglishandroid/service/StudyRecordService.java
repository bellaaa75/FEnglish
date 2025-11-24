package com.example.fenglishandroid.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StudyRecordService {
    // 学习记录统计接口
    @GET("api/study-records/statistics")
    Call<ResponseBody> getStudyStatistics(@Query("userId") String userId);
}
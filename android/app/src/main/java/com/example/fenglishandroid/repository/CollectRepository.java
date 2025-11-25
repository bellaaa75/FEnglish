package com.example.fenglishandroid.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.CollectWordDTO;
import com.example.fenglishandroid.model.CollectBookDTO;
import com.example.fenglishandroid.model.WordSection;
import com.example.fenglishandroid.service.RetrofitClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Collections;
public class CollectRepository {

    /* ================ 单词收藏 ================ */
    public void collectWord(String wordId,
                            Runnable onOk,
                            Runnable onErr) {
        RetrofitClient.getCollectApi()
                .collectWord(wordId)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call,
                                           Response<BaseResponse<Void>> res) {
                        if (res.isSuccessful() && res.body() != null && res.body().getCode() == 200) {
                            onOk.run();
                        } else {
                            onErr.run();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        onErr.run();
                    }
                });
    }

    public void unCollectWord(String wordId,
                              Runnable onOk,
                              Runnable onErr) {
        RetrofitClient.getCollectApi()
                .unCollectWord(wordId)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call,
                                           Response<BaseResponse<Void>> res) {
                        if (res.isSuccessful() && res.body() != null && res.body().getCode() == 200) {
                            onOk.run();
                        } else {
                            onErr.run();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        onErr.run();
                    }
                });
    }

    public void loadWordCollects(int page, int size, MutableLiveData<List<WordSection>> sectionLive,MutableLiveData<String> error) {
        RetrofitClient.getCollectApi()
                .myWordCollects(page, size)
                .enqueue(new Callback<BaseResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Map<String, Object>>> call, Response<BaseResponse<Map<String, Object>>> res) {
                        Log.d("CollectRepository", "🔥 单词 onResponse 被调用，code=" + res.code());
                        if (res.isSuccessful() && res.body() != null && res.body().getCode() == 200) {
                            Log.d("CollectRepository", "✅ 单词进入成功分支");
                            Map<String, Object> map = res.body().getData();
                            List<?> rawList = (List<?>) map.get("content");

                            List<CollectWordDTO> dtoList = new ArrayList<>();

                            for (Object o : rawList) {
                                Map<String, Object> item = (Map<String, Object>) o;
                                CollectWordDTO dto = new CollectWordDTO();
                                dto.setCollectId((String) item.get("collectId"));
                                dto.setTargetId((String) item.get("targetId"));
                                dto.setWordName((String) item.get("wordName"));
                                String fullExplain = (String) item.get("wordExplain");
                                String firstExplain = fullExplain == null ? "" : fullExplain.split("；")[0];
                                dto.setWordExplain(firstExplain);

                                // 时间字符串 → Date（注意格式带 T）
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                try {
                                    String timeStr = (String) item.get("collectTime");
                                    Date date = sdf.parse(timeStr);
                                    dto.setCollectTime(date);
                                } catch (Exception e) {
                                    dto.setCollectTime(new Date()); // 失败就用当前时间
                                }
                                dtoList.add(dto);
                            }

                            // 按收藏时间倒序排序
                            Collections.sort(dtoList, (a, b) -> b.getCollectTime().compareTo(a.getCollectTime()));

                            // 分组处理
                            List<WordSection> sections = groupToSections(dtoList);
                            Log.d("CollectRepository", "分组后的段数: " + sections.size());
                            sectionLive.postValue(sections);
                        } else {
                            Log.d("CollectRepository", "❌ 单词未进入成功分支");
                            error.postValue("加载失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Map<String, Object>>> call, Throwable t) {
                        error.postValue("网络错误");
                    }
                });
    }

//    private List<CollectWordDTO> groupByDate(List<CollectWordDTO> list) {
//        Map<Date, List<CollectWordDTO>> map = new TreeMap<>((a, b) -> b.compareTo(a));
//        for (CollectWordDTO dto : list) {
//            Date date = new Date(dto.getCollectTime().getTime());
//            map.computeIfAbsent(date, k -> new ArrayList<>()).add(dto);
//        }
//
//        List<CollectWordDTO> result = new ArrayList<>();
//        for (Map.Entry<Date, List<CollectWordDTO>> entry : map.entrySet()) {
//            Date date = entry.getKey();
//            List<CollectWordDTO> group = entry.getValue();
//
//            // 添加一个虚拟的日期标题对象
//            CollectWordDTO header = new CollectWordDTO();
//            header.setShowDateHeader(true);
//            header.setCollectTime(date);
//            result.add(header);
//
//            // 添加分组内的单词
//            result.addAll(group);
//        }
//        return result;
//    }

    /**
     * 把扁平单词列表按 UTC 日期分段，返回 List<WordSection>
     * 不再插入虚拟 DTO
     */
    private List<WordSection> groupToSections(List<CollectWordDTO> flatList) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<String, WordSection> map = new LinkedHashMap<>(); // 保持插入顺序
        for (CollectWordDTO dto : flatList) {
            String day = fmt.format(dto.getCollectTime());
            WordSection sec = map.computeIfAbsent(day, k -> {
                WordSection s = new WordSection();
                s.date = day;
                s.words = new ArrayList<>();
                return s;
            });
            sec.words.add(dto);
        }
        return new ArrayList<>(map.values());
    }

    /* ================ 单词书收藏 ================ */
    public void collectBook(String bookId,
                            Runnable onOk,
                            Runnable onErr) {
        RetrofitClient.getCollectApi()
                .collectBook(bookId)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call,
                                           Response<BaseResponse<Void>> res) {
                        if (res.isSuccessful() && (res.body() != null) && res.body().getCode() == 200) {
                            onOk.run();
                        } else {
                            onErr.run();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        onErr.run();
                    }
                });
    }

    public void unCollectBook(String bookId,
                              Runnable onOk,
                              Runnable onErr) {
        RetrofitClient.getCollectApi()
                .unCollectBook(bookId)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call,
                                           Response<BaseResponse<Void>> res) {
                        if (res.isSuccessful() && res.body() != null && res.body().getCode() == 200) {
                            onOk.run();
                        } else {
                            onErr.run();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        onErr.run();
                    }
                });
    }

    public void loadBookCollects(int page, int size,
                                 MutableLiveData<List<CollectBookDTO>> live,
                                 MutableLiveData<String> error) {

        RetrofitClient.getCollectApi()
                .myBookCollects(page, size)
                .enqueue(new Callback<BaseResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Map<String, Object>>> call,
                                           Response<BaseResponse<Map<String, Object>>> res) {
                        Log.d("CollectRepository", "🔥 onResponse 被调用，code=" + res.code());
                        if (res.isSuccessful() && res.body() != null && res.body().getCode() == 200) {
                            Log.d("CollectRepository", "✅ 进入成功分支");
                            Map<String, Object> map = res.body().getData();
                            List<?> rawList = (List<?>) map.get("content");

                            /* ========== 字符串 → Date 转换 ========== */
                            List<CollectBookDTO> dtoList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                            for (Object o : rawList) {
                                Map<String, Object> item = (Map<String, Object>) o;
                                CollectBookDTO dto = new CollectBookDTO();

                                dto.setCollectId((String) item.get("collectId"));
                                dto.setTargetId((String) item.get("targetId"));
                                dto.setBookName((String) item.get("bookName"));

                                // 关键：把字符串转 Date
                                try {
                                    String publishStr = (String) item.get("publishTime");
                                    String collectStr = (String) item.get("collectTime");
                                    dto.setPublishTime(sdf.parse(publishStr));
                                    dto.setCollectTime(sdf.parse(collectStr));

                                    Log.d("CollectRepository", "解析后的 publishTime：" + dto.getPublishTime());
                                    Log.d("CollectRepository", "解析后的 collectTime：" + dto.getCollectTime());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dto.setPublishTime(new Date());
                                    dto.setCollectTime(new Date());
                                }
                                dtoList.add(dto);
                            }
                            /* ======================================= */
                            // ✅ 日志 + 分发 放在这里
                            Log.d("CollectRepository", "准备 post 书单数据，条数=" + dtoList.size());

                            live.postValue(dtoList);
                        } else {
                            Log.d("CollectRepository", "❌ 未进入成功分支，body=" + res.body());
                            error.postValue("加载失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Map<String, Object>>> call, Throwable t) {
                        error.postValue("网络错误");
                    }
                });

    }
}
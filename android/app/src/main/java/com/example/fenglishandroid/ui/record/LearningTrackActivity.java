package com.example.fenglishandroid.ui.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.StudyRecordService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningTrackActivity extends AppCompatActivity {
    // 控件
    private ImageView ivBack;
    private TextView tvUserId, tvMonthlyWord, tvMonthlyDays;
    private GridView gvCalendar;
    private LineChart lineChart;

    // 数据
    private String userId; // 从MeFragment传递的用户ID
    private int monthlyWordCount; // 本月学习单词数
    private int monthlyStudyDays; // 本月打卡天数
    private List<Map<String, Object>> dailyWordCounts = new ArrayList<>(); // 每日学习数据
    private List<String> calendarDates = new ArrayList<>(); // 本月日期列表
    private List<Integer> calendarWordCounts = new ArrayList<>(); // 对应日期的单词数

    // 服务接口
    private StudyRecordService studyRecordService;
    private Gson gson = new Gson();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_track);

        // 获取从MeFragment传递的用户ID
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // 初始化服务
        studyRecordService = RetrofitClient.getStudyRecordService();

        initView();
        initListener();
        fetchStudyStatistics(); // 调用后端接口获取统计数据
        initCalendar(); // 初始化本月日历日期
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        tvUserId = findViewById(R.id.tv_user_id);
        tvMonthlyWord = findViewById(R.id.tv_monthly_word);
        tvMonthlyDays = findViewById(R.id.tv_monthly_days);
        gvCalendar = findViewById(R.id.gv_calendar);
        lineChart = findViewById(R.id.line_chart);

        // 显示用户ID
        tvUserId.setText("用户ID：" + userId);
    }

    private void initListener() {
        // 返回按钮：关闭页面，返回MeFragment
        ivBack.setOnClickListener(v -> finish());
    }

    // 调用后端接口获取学习统计数据
    private void fetchStudyStatistics() {
        Call<ResponseBody> call = studyRecordService.getStudyStatistics(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("LearningTrackDebug", "统计数据：" + json);

                        // 解析后端返回数据
                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        if ("200".equals(result.get("code").toString())) {
                            Map<String, Object> data = (Map<String, Object>) result.get("data");
                            monthlyWordCount = ((Double) data.get("monthlyWordCount")).intValue();
                            monthlyStudyDays = ((Double) data.get("monthlyStudyDays")).intValue();
                            dailyWordCounts = gson.fromJson(
                                    gson.toJson(data.get("dailyWordCounts")),
                                    new TypeToken<List<Map<String, Object>>>() {}.getType()
                            );

                            // 更新UI
                            updateUserInfo();
                            updateCalendarData();
                            updateLineChart();
                        } else {
                            showToast("获取数据失败：" + result.get("message"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("解析失败");
                    }
                } else {
                    showToast("请求失败，状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("网络错误：" + t.getMessage());
                Log.e("LearningTrackDebug", "请求失败：" + t.getMessage(), t);
            }
        });
    }

    // 初始化本月日历的日期列表（例如：2025-11-01 到 2025-11-30）
    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0表示1月，需注意
        calendar.set(year, month, 1); // 定位到本月1号

        // 获取本月第一天是星期几（1=周日，2=周一...7=周六，可根据需求调整）
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 填充本月前的空日期（用空字符串占位）
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDates.add("");
            calendarWordCounts.add(0);
        }

        // 填充本月日期
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 1; i <= daysInMonth; i++) {
            calendar.set(year, month, i);
            calendarDates.add(sdf.format(calendar.getTime()));
            calendarWordCounts.add(0); // 初始化为0
        }
    }

    // 更新用户信息（本月学习数、打卡天数）
    private void updateUserInfo() {
        tvMonthlyWord.setText("本月学习单词数：" + monthlyWordCount);
        tvMonthlyDays.setText("本月打卡天数：" + monthlyStudyDays);
    }

    // 更新日历表格的学习数据
    private void updateCalendarData() {
        // 将后端返回的每日数据匹配到日历中
        for (Map<String, Object> dailyData : dailyWordCounts) {
            String studyDate = (String) dailyData.get("studyDate");
            int wordCount = ((Double) dailyData.get("wordCount")).intValue();
            int index = calendarDates.indexOf(studyDate);
            if (index != -1) {
                calendarWordCounts.set(index, wordCount);
            }
        }

        // 设置日历GridView的适配器
        gvCalendar.setAdapter(new CalendarAdapter());
    }

    // 日历GridView适配器
    private class CalendarAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return calendarDates.size();
        }

        @Override
        public Object getItem(int position) {
            return calendarDates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_calendar_day, parent, false);
            }

            TextView tvDate = convertView.findViewById(R.id.tv_date);
            TextView tvWordCount = convertView.findViewById(R.id.tv_word_count);

            String date = calendarDates.get(position);
            int wordCount = calendarWordCounts.get(position);

            // 空日期（本月前/后的占位）
            if (date.isEmpty()) {
                tvDate.setText("");
                tvWordCount.setText("");
                convertView.setBackgroundColor(getResources().getColor(R.color.gray_light));
            } else {
                // 显示日期（仅显示日，如“04”）
                String day = date.split("-")[2];
                tvDate.setText(day);
                // 显示单词数
                tvWordCount.setText(String.valueOf(wordCount));
                // 有学习数据的日期设置蓝色背景
                if (wordCount > 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.blue_light));
                } else {
                    convertView.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
            return convertView;
        }
    }

    // 更新折线图（近7天学习数据）
    private void updateLineChart() {
        // 筛选近7天的数据
        List<Entry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // 7天前

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
            String label = sdf.format(date);
            xLabels.add(label);

            // 查找对应日期的单词数
            int wordCount = 0;
            for (Map<String, Object> dailyData : dailyWordCounts) {
                if (dateStr.equals(dailyData.get("studyDate"))) {
                    wordCount = ((Double) dailyData.get("wordCount")).intValue();
                    break;
                }
            }
            entries.add(new Entry(i, wordCount));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // 配置折线图
        LineDataSet dataSet = new LineDataSet(entries, "学习单词数");
        dataSet.setColor(getResources().getColor(R.color.blue));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        LineData lineData = new LineData(dataSet);

        // 配置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabels.get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 隐藏描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        // 设置数据并刷新
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    // 自定义Toast
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
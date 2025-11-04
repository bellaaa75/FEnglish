package org.example.fenglish.vo.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StudyStatisticVO {
    // 本月学习单词数
    private long monthlyWordCount;

    // 本月打卡天数
    private long monthlyStudyDays;

    // 每天学习单词数（key:日期字符串, value:单词数）
    private List<Map<String, Object>> dailyWordCounts;

    public long getMonthlyWordCount() {
        return monthlyWordCount;
    }

    public void setMonthlyWordCount(long monthlyWordCount) { // 关键：添加此 setter
        this.monthlyWordCount = monthlyWordCount;
    }

    public long getMonthlyStudyDays() {
        return monthlyStudyDays;
    }

    public void setMonthlyStudyDays(long monthlyStudyDays) { // 关键：添加此 setter
        this.monthlyStudyDays = monthlyStudyDays;
    }

    public List<Map<String, Object>> getDailyWordCounts() {
        return dailyWordCounts;
    }

    public void setDailyWordCounts(List<Map<String, Object>> dailyWordCounts) { // 关键：添加此 setter
        this.dailyWordCounts = dailyWordCounts;
    }
}
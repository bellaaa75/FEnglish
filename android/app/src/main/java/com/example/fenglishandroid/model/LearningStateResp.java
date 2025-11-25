package com.example.fenglishandroid.model;

/**
 * 学习状态实体类（仅新增，不影响原有代码）
 */
public class LearningStateResp {
    private String userId;
    private String wordId;
    private String wordName;
    private String wordExplain;
    private String learnState; // 可能值："未学"、"已学"、"熟练掌握"

    // getter + setter（必须，Gson解析需要）
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getWordName() { return wordName; }
    public void setWordName(String wordName) { this.wordName = wordName; }
    public String getWordExplain() { return wordExplain; }
    public void setWordExplain(String wordExplain) { this.wordExplain = wordExplain; }
    public String getLearnState() { return learnState; }
    public void setLearnState(String learnState) { this.learnState = learnState; }
}
package com.example.fenglishandroid.model.request;

/**
 * 增加学习记录的请求体实体类（仅新增）
 */
public class StudyRecordRequest {
    private String studyTime; // 格式：yyyy-MM-dd HH:mm:ss
    private User user;
    private EnglishWord englishWord;

    // 内部类：user对象（匹配请求体结构）
    public static class User {
        private String userId;

        public User(String userId) {
            this.userId = userId;
        }

        // getter + setter
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    // 内部类：englishWord对象（匹配请求体结构）
    public static class EnglishWord {
        private String wordId;

        public EnglishWord(String wordId) {
            this.wordId = wordId;
        }

        // getter + setter
        public String getWordId() { return wordId; }
        public void setWordId(String wordId) { this.wordId = wordId; }
    }

    // 构造方法（简化对象创建）
    public StudyRecordRequest(String studyTime, String userId, String wordId) {
        this.studyTime = studyTime;
        this.user = new User(userId);
        this.englishWord = new EnglishWord(wordId);
    }

    // getter + setter（Gson序列化需要）
    public String getStudyTime() { return studyTime; }
    public void setStudyTime(String studyTime) { this.studyTime = studyTime; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public EnglishWord getEnglishWord() { return englishWord; }
    public void setEnglishWord(EnglishWord englishWord) { this.englishWord = englishWord; }
}
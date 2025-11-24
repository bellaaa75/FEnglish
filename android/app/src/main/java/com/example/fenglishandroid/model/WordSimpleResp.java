package com.example.fenglishandroid.model;

// 原有字段保留，新增详情接口的字段（与后端返回一致）
public class WordSimpleResp {
    // 原有字段（仓库已存在）
    private String wordId;
    private String wordName;
    private String partOfSpeech;
    private String wordExplain;

    // 新增：详情接口的额外字段（与后端返回1:1对应）
    private String thirdPersonSingular;
    private String presentParticiple;
    private String pastParticiple;
    private String pastTense;
    private String exampleSentence;

    // 原有字段的 getter/setter（仓库已存在，保留）
    public String getWordId() { return wordId; }
    public void setWordId(String wordID) { this.wordId = wordID; }
    public String getWordName() { return wordName; }
    public void setWordName(String wordName) { this.wordName = wordName; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public String getWordExplain() { return wordExplain; }
    public void setWordExplain(String wordExplain) { this.wordExplain = wordExplain; }

    // 新增字段的 getter（用于详情页展示）
    public String getThirdPersonSingular() { return thirdPersonSingular; }
    public String getPresentParticiple() { return presentParticiple; }
    public String getPastParticiple() { return pastParticiple; }
    public String getPastTense() { return pastTense; }
    public String getExampleSentence() { return exampleSentence; }

    // 新增字段的 setter（Retrofit 解析 JSON 需要）
    public void setThirdPersonSingular(String thirdPersonSingular) { this.thirdPersonSingular = thirdPersonSingular; }
    public void setPresentParticiple(String presentParticiple) { this.presentParticiple = presentParticiple; }
    public void setPastParticiple(String pastParticiple) { this.pastParticiple = pastParticiple; }
    public void setPastTense(String pastTense) { this.pastTense = pastTense; }
    public void setExampleSentence(String exampleSentence) { this.exampleSentence = exampleSentence; }
}
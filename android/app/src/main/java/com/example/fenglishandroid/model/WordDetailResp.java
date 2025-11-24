package com.example.fenglishandroid.model;

// 完全匹配详情接口返回字段（无Result包裹）
public class WordDetailResp {
    private String wordName;        // 后端字段：wordName
    private String partOfSpeech;    // 后端字段：partOfSpeech
    private String thirdPersonSingular; // 后端字段：thirdPersonSingular
    private String presentParticiple;   // 后端字段：presentParticiple
    private String pastParticiple;      // 后端字段：pastParticiple
    private String pastTense;           // 后端字段：pastTense
    private String wordExplain;     // 后端字段：wordExplain
    private String exampleSentence; // 后端字段：exampleSentence
    private String wordID;          // 后端字段：wordID（详情接口中是字符串，如"WORD_91aa8cddd7"）

    // 所有字段的getter（必须，Retrofit解析JSON用）
    public String getWordName() { return wordName; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public String getThirdPersonSingular() { return thirdPersonSingular; }
    public String getPresentParticiple() { return presentParticiple; }
    public String getPastParticiple() { return pastParticiple; }
    public String getPastTense() { return pastTense; }
    public String getWordExplain() { return wordExplain; }
    public String getExampleSentence() { return exampleSentence; }
    public String getWordID() { return wordID; }
}
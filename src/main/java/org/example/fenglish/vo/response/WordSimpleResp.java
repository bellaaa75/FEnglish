package org.example.fenglish.vo.response;

import lombok.Data;

/**
 * 单词简化响应VO（用于单词书详情中展示关联的单词列表）
 */
@Data
public class WordSimpleResp {
    /**
     * 单词ID（对应EnglishWords的wordId）
     */
    private String wordId;

    /**
     * 单词名称（如"abandon"）
     */
    private String wordName;

    /**
     * 词性（如"vt./n."）
     */
    private String partOfSpeech;

    /**
     * 单词释义（核心含义，供前端快速展示）
     */
    private String wordExplain;

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordExplain() {
        return wordExplain;
    }

    public void setWordExplain(String wordExplain) {
        this.wordExplain = wordExplain;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}
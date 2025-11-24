package com.example.fenglishandroid.model;

public class WordSimpleResp {
    private String wordId;
    private String wordName;
    private String partOfSpeech;
    private String wordExplain;

    private boolean isCollected;

    // getter和setter
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getWordName() { return wordName; }
    public void setWordName(String wordName) { this.wordName = wordName; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public String getWordExplain() { return wordExplain; }
    public void setWordExplain(String wordExplain) { this.wordExplain = wordExplain; }
    public boolean isCollected() { return isCollected; }
    public void setCollected(boolean collected) { isCollected = collected; }
}
package com.example.fenglishandroid.model;

public class WordSimpleResp {
    private String wordId;
    private String wordName;
    private String partOfSpeech;
    private String wordExplain;
    private boolean isCollected;
    public boolean isCollected() { return isCollected; }
    public void setCollected(boolean collected) { isCollected = collected; }
    /* ========== 新增字段 ========== */
    private String thirdPersonSingular;
    private String presentParticiple;
    private String pastTense;
    private String pastParticiple;
    private String exampleSentence;

    // getter和setter
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getWordName() { return wordName; }
    public void setWordName(String wordName) { this.wordName = wordName; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public String getWordExplain() { return wordExplain; }
    public void setWordExplain(String wordExplain) { this.wordExplain = wordExplain; }

    /* ========== 新增 getter/setter ========== */
    public String getThirdPersonSingular() {
        return thirdPersonSingular;
    }
    public void setThirdPersonSingular(String thirdPersonSingular) {
        this.thirdPersonSingular = thirdPersonSingular;
    }

    public String getPresentParticiple() {
        return presentParticiple;
    }
    public void setPresentParticiple(String presentParticiple) {
        this.presentParticiple = presentParticiple;
    }

    public String getPastTense() {
        return pastTense;
    }
    public void setPastTense(String pastTense) {
        this.pastTense = pastTense;
    }

    public String getPastParticiple() {
        return pastParticiple;
    }
    public void setPastParticiple(String pastParticiple) {
        this.pastParticiple = pastParticiple;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }
    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }
}
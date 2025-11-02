package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "EnglishWords")
public class EnglishWords {

    @Id
    @Column(name = "WordID", length = 50)
    private String wordId;

    @Column(name = "WordName", nullable = false, length = 100)
    private String wordName;

    @Column(name = "PartOfSpeech", length = 50)
    private String partOfSpeech;

    @Column(name = "ThirdPersonSingular", length = 100)
    private String thirdPersonSingular;

    @Column(name = "PresentParticiple", length = 100)
    private String presentParticiple;

    @Column(name = "PastParticiple", length = 100)
    private String pastParticiple;

    @Column(name = "PastTense", length = 100)
    private String pastTense;

    @Column(name = "WordExplain", columnDefinition = "TEXT", nullable = false)
    private String wordExplain;

    @Column(name = "ExampleSentence", columnDefinition = "TEXT")
    private String exampleSentence;

    // 按照需求报告添加的业务方法
    public String getWordID() {
        return this.wordId;
    }

    public boolean setWordID(String wordId) {
        try {
            this.wordId = wordId;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getWordExplain() {
        return this.wordExplain;
    }

    public boolean setWordExplain(String wordExplain) {
        try {
            this.wordExplain = wordExplain;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getWordName() {
        return this.wordName;
    }

    public boolean setWordName(String wordName) {
        try {
            this.wordName = wordName;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 添加需求报告中没有但实际需要的getter方法
    public String getPartOfSpeech() {
        return this.partOfSpeech;
    }

    public String getThirdPersonSingular() {
        return this.thirdPersonSingular;
    }

    public String getPresentParticiple() {
        return this.presentParticiple;
    }

    public String getPastParticiple() {
        return this.pastParticiple;
    }

    public String getPastTense() {
        return this.pastTense;
    }

    public String getExampleSentence() {
        return this.exampleSentence;
    }

    // 添加setter方法（需求报告没有要求，但实际需要）
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public void setThirdPersonSingular(String thirdPersonSingular) {
        this.thirdPersonSingular = thirdPersonSingular;
    }

    public void setPresentParticiple(String presentParticiple) {
        this.presentParticiple = presentParticiple;
    }

    public void setPastParticiple(String pastParticiple) {
        this.pastParticiple = pastParticiple;
    }

    public void setPastTense(String pastTense) {
        this.pastTense = pastTense;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }
}
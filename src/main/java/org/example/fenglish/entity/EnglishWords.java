package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "EnglishWords")
public class EnglishWords {

    @Id
    @Column(name = "WordID", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private String wordId;

    @Column(name = "WordName", nullable = false, length = 100) // 单词名非空
    private String wordName;

    @Column(name = "PartOfSpeech", length = 50) // 词性（如n./v.）
    private String partOfSpeech;

    @Column(name = "ThirdPersonSingular", length = 100) // 第三人称单数
    private String thirdPersonSingular;

    @Column(name = "PresentParticiple", length = 100) // 现在分词
    private String presentParticiple;

    @Column(name = "PastParticiple", length = 100) // 过去分词
    private String pastParticiple;

    @Column(name = "PastTense", length = 100) // 过去式
    private String pastTense;

    @Column(name = "WordExplain", columnDefinition = "TEXT", nullable = false) // 释义非空
    private String wordExplain;

    @Column(name = "ExampleSentence", columnDefinition = "TEXT") // 例句
    private String exampleSentence;
}
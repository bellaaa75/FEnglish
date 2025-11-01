package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "EnglishWords")
public class EnglishWords {

    @Id
    @Column(name = "WordID")
    private String wordId;

    @Column(name = "WordName", nullable = false)
    private String wordName;

    @Column(name = "PartOfSpeech")
    private String partOfSpeech;

    @Column(name = "ThirdPersonSingular")
    private String thirdPersonSingular;

    @Column(name = "PresentParticiple")
    private String presentParticiple;

    @Column(name = "PastParticiple")
    private String pastParticiple;

    @Column(name = "PastTense")
    private String pastTense;

    @Column(name = "WordExplain", columnDefinition = "TEXT")
    private String wordExplain;

    @Column(name = "ExampleSentence", columnDefinition = "TEXT")
    private String exampleSentence;
}
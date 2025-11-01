package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "WordInBook")
@IdClass(WordInBook.WordInBookId.class)
public class WordInBook {

    @Id // 两个字段都标记为@Id，组成复合主键
    @Column(name = "WordID")
    private String wordId;

    @Id
    @Column(name = "BookID")
    private String bookId;

    // 复合主键类
    @Data
    public static class WordInBookId implements Serializable {
        private String wordId;
        private String bookId;
    }
}
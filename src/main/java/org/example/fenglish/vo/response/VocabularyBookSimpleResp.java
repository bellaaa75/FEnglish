package org.example.fenglish.vo.response;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class VocabularyBookSimpleResp {
    private String bookId;
    private String bookName;
    private LocalDateTime publishTime;
}
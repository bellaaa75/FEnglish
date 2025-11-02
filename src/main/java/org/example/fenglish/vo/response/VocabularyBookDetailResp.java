package org.example.fenglish.vo.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class VocabularyBookDetailResp {
    private String bookId;
    private String bookName;
    private Date publishTime;
    private List<WordSimpleResp> wordList;  // 关联的单词列表
}
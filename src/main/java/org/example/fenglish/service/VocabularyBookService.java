package org.example.fenglish.service;

import org.example.fenglish.entity.VocabularyBook;
import org.example.fenglish.vo.request.VocabularyBookAddReq;
import org.example.fenglish.vo.request.VocabularyBookUpdateReq;
import org.example.fenglish.vo.response.VocabularyBookDetailResp;
import java.util.List;

public interface VocabularyBookService {
    // 1. 新增单词书
    void addVocabularyBook(VocabularyBookAddReq req, String adminId);

    // 2. 删除单词书（含关联记录）
    void deleteVocabularyBook(String bookId, String adminId);

    // 3. 修改单词书（仅修改名称、发布时间）
    void updateVocabularyBook(String bookId, VocabularyBookUpdateReq req, String adminId);

    // 4. 查看单个单词书详情（含关联单词）
    VocabularyBookDetailResp getVocabularyBookDetail(String bookId);

}
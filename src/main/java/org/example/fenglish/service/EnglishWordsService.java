package org.example.fenglish.service;

import org.example.fenglish.entity.EnglishWords;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EnglishWordsService {

    boolean addWords(EnglishWords word);          // 增加单词
    boolean deleteWords(String wordId);           // 删除单词
    boolean editWords(String wordId, EnglishWords word); // 修改单词

    // 查询方法
    EnglishWords getWordById(String wordId);      // 通过ID获取单词（用于实现getWordID等方法）
    EnglishWords getWordByName(String wordName);  // 通过名称获取单词
    List<EnglishWords> getWordsByFuzzyName(String wordName);
    Page<EnglishWords> getWordList(int pageNum, int pageSize);
}
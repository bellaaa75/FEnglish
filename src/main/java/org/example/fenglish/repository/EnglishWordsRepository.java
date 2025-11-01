package org.example.fenglish.repository;

import org.example.fenglish.entity.EnglishWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnglishWordsRepository extends JpaRepository<EnglishWords, String> {

    // 根据单词名查找
    EnglishWords findByWordName(String wordName);

    // 模糊搜索单词
    List<EnglishWords> findByWordNameContaining(String keyword);

    // 根据词性查找
    List<EnglishWords> findByPartOfSpeech(String partOfSpeech);
}
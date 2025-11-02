package org.example.fenglish.repository;

import org.example.fenglish.entity.VocabularyBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyBookRepository extends JpaRepository<VocabularyBook, String> {

    // 根据单词书名称模糊查询（支持前端搜索）
    List<VocabularyBook> findByBookNameContaining(String bookName);


    // 检查单词书名称是否已存在
    boolean existsByBookName(String bookName);

    // 关联查询：查询单词书及关联的单词（包含单词名、词性等详情）
    @Query("SELECT vb FROM VocabularyBook vb " +
            "LEFT JOIN FETCH vb.wordInBooks wib " +
            "LEFT JOIN FETCH wib.englishWords ew " +
            "WHERE vb.bookId = :bookId")
    Optional<VocabularyBook> findByBookIdWithWords(@Param("bookId") String bookId);
}
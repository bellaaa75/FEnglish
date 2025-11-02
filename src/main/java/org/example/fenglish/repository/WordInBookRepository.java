package org.example.fenglish.repository;

import org.example.fenglish.entity.WordInBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordInBookRepository extends JpaRepository<WordInBook, WordInBook.WordInBookId> {

    // 根据单词书ID查询所有关联的单词
    List<WordInBook> findByBookId(String bookId);

    // 检查单词是否已在单词书中（通过wordId）
    Optional<WordInBook> findByBookIdAndWordId(String bookId, String wordId);

    // 通过单词书ID和单词名检查关联（适配EnglishWords的wordName查询）
    @Query("SELECT wib FROM WordInBook wib " +
            "JOIN wib.englishWords ew " +
            "WHERE wib.bookId = :bookId AND ew.wordName = :wordName")
    Optional<WordInBook> findByBookIdAndWordName(@Param("bookId") String bookId, @Param("wordName") String wordName);

    // 根据单词书ID删除所有关联记录
    @Modifying
    @Query("DELETE FROM WordInBook wib WHERE wib.bookId = :bookId")
    void deleteAllByBookId(@Param("bookId") String bookId);

    // 从单词书删除指定单词（通过wordId）
    @Modifying
    @Query("DELETE FROM WordInBook wib WHERE wib.bookId = :bookId AND wib.wordId = :wordId")
    void deleteByBookIdAndWordId(@Param("bookId") String bookId, @Param("wordId") String wordId);

    //从单词书删除指定单词（通过wordName，适配按单词名操作）
    @Transactional
    @Modifying
    @Query("DELETE FROM WordInBook wib WHERE wib.bookId = :bookId AND wib.wordId IN " +
            "(SELECT ew.wordId FROM EnglishWords ew WHERE ew.wordName = :wordName)")
    void deleteByBookIdAndWordName(@Param("bookId") String bookId, @Param("wordName") String wordName);
}
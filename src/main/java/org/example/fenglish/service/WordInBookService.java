package org.example.fenglish.service;

import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.entity.VocabularyBook;
import org.example.fenglish.entity.WordInBook;
import org.example.fenglish.repository.EnglishWordsRepository;
import org.example.fenglish.repository.VocabularyBookRepository;
import org.example.fenglish.repository.WordInBookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class WordInBookService {

    private final WordInBookRepository wordInBookRepository;
    private final VocabularyBookRepository vocabularyBookRepository;
    private final EnglishWordsRepository englishWordsRepository;

    public WordInBookService(WordInBookRepository wordInBookRepository, VocabularyBookRepository vocabularyBookRepository, EnglishWordsRepository englishWordsRepository) {
        this.wordInBookRepository = wordInBookRepository;
        this.vocabularyBookRepository = vocabularyBookRepository;
        this.englishWordsRepository = englishWordsRepository;
    }

    // 1. 向单词书添加单词
    public void addWordToBook(String bookId, String wordId, String adminId) {
        // 校验1：单词书是否存在
        VocabularyBook book = vocabularyBookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("单词书不存在"));

        // 校验2：单词是否存在
        if (!englishWordsRepository.existsById(wordId)) {
            throw new EntityNotFoundException("单词不存在，无法添加");
        }
        // 校验3：单词是否已在单词书中
        if (wordInBookRepository.findByBookIdAndWordId(bookId, wordId).isPresent()) {
            throw new IllegalArgumentException("该单词已在单词书中，无需重复添加");
        }
        // 构建关联记录并保存
        WordInBook wordInBook = new WordInBook();
        wordInBook.setBookId(bookId);
        wordInBook.setWordId(wordId);
        wordInBookRepository.save(wordInBook);
    }

    // 2. 从单词书删除单词
    public void deleteWordFromBook(String bookId, String wordId, String adminId) {
        // 校验1：单词书是否存在且有权操作
        VocabularyBook book = vocabularyBookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("单词书不存在"));
        // 校验2：单词是否在单词书中
        if (wordInBookRepository.findByBookIdAndWordId(bookId, wordId).isEmpty()) {
            throw new EntityNotFoundException("该单词不在单词书中，无法删除");
        }
        // 删除关联记录
        wordInBookRepository.deleteByBookIdAndWordId(bookId, wordId);
    }

    // 3. 查看单词书下的所有单词（复用VocabularyBookService的getVocabularyBookDetail，可选）
    public List<WordInBook> getWordsInBook(String bookId) {
        return wordInBookRepository.findByBookId(bookId);
    }
}
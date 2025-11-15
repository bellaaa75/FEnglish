package org.example.fenglish.service.impl;  // 统一放在service.impl包下

import org.example.fenglish.entity.VocabularyBook;
import org.example.fenglish.repository.VocabularyBookRepository;
import org.example.fenglish.repository.WordInBookRepository;
import org.example.fenglish.service.VocabularyBookService;
import org.example.fenglish.vo.request.VocabularyBookAddReq;
import org.example.fenglish.vo.request.VocabularyBookUpdateReq;
import org.example.fenglish.vo.response.VocabularyBookDetailResp;
import org.example.fenglish.vo.response.WordSimpleResp;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional  // 事务管理，所有方法加事务
public class VocabularyBookServiceImpl implements VocabularyBookService {

    private final VocabularyBookRepository vocabularyBookRepository;
    private final WordInBookRepository wordInBookRepository;

    public VocabularyBookServiceImpl(WordInBookRepository wordInBookRepository, VocabularyBookRepository vocabularyBookRepository) {
        this.wordInBookRepository = wordInBookRepository;
        this.vocabularyBookRepository = vocabularyBookRepository;
    }

    // 1. 新增单词书
    @Override
    public void addVocabularyBook(VocabularyBookAddReq req, String adminId) {
        // 校验1：单词书名称是否已存在
        if (vocabularyBookRepository.existsByBookName(req.getBookName())) {
            throw new IllegalArgumentException("单词书名称已存在，请更换名称");
        }
        // 构建单词书实体
        VocabularyBook book = new VocabularyBook();
        // 处理bookId：若前端未输入则自动生成"BOOK_随机字符串"，否则用前端输入
        String generatedId = "BOOK_" + UUID.randomUUID().toString().replace("-", "");
        book.setBookId(req.getBookId() == null ? generatedId : req.getBookId());
        book.setBookName(req.getBookName());
        // 处理发布时间：若前端未选择则默认当前时间
        book.setPublishTime(req.getPublishTime() == null ? new Date() : req.getPublishTime());
        // 保存到数据库
        vocabularyBookRepository.save(book);
    }

    // 2. 删除单词书（同步删除关联的单词记录）
    @Override
    public void deleteVocabularyBook(String bookId, String adminId) {
        // 校验：单词书是否存在
        VocabularyBook book = vocabularyBookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("单词书不存在，无法删除"));
        // 步骤1：删除单词书与单词的关联记录（避免外键约束）
        wordInBookRepository.deleteAllByBookId(bookId);
        // 步骤2：删除单词书本身
        vocabularyBookRepository.delete(book);
    }

    // 3. 修改单词书
    @Override
    public void updateVocabularyBook(String bookId, VocabularyBookUpdateReq req, String adminId) {
        // 校验1：单词书是否存在
        VocabularyBook book = vocabularyBookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("单词书不存在，无法修改"));
        // 校验2：若修改名称，需检查新名称是否已存在
        if (req.getBookName() != null && !req.getBookName().equals(book.getBookName())) {
            if (vocabularyBookRepository.existsByBookName(req.getBookName())) {
                throw new IllegalArgumentException("新单词书名称已存在，请更换名称");
            }
            book.setBookName(req.getBookName());
        }
        // 修改发布时间（若前端传入）
        if (req.getPublishTime() != null) {
            book.setPublishTime(req.getPublishTime());
        }
        // 保存修改
        vocabularyBookRepository.save(book);
    }

    // 4. 查看单词书详情（含关联的单词列表）
    @Override
    public VocabularyBookDetailResp getVocabularyBookDetail(String bookId) {
        // 关联查询单词书及单词
        VocabularyBook book = vocabularyBookRepository.findByBookIdWithWords(bookId)
                .orElseThrow(() -> new EntityNotFoundException("单词书不存在"));
        // 转换为VO（避免返回冗余字段，前端仅需必要数据）
        VocabularyBookDetailResp resp = new VocabularyBookDetailResp();
        resp.setBookId(book.getBookId());
        resp.setBookName(book.getBookName());
        resp.setPublishTime(book.getPublishTime());
        // 转换单词列表（仅返回单词ID、名称、词性、释义）
        List<WordSimpleResp> wordList = book.getWordInBooks().stream()
                .map(wib -> {
                    WordSimpleResp wordResp = new WordSimpleResp();
                    wordResp.setWordId(wib.getEnglishWords().getWordId());
                    wordResp.setWordName(wib.getEnglishWords().getWordName());
                    wordResp.setPartOfSpeech(wib.getEnglishWords().getPartOfSpeech());
                    wordResp.setWordExplain(wib.getEnglishWords().getWordExplain());
                    return wordResp;
                })
                .collect(Collectors.toList());
        resp.setWordList(wordList);
        return resp;
    }
}
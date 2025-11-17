package org.example.fenglish.service.impl;  // 统一放在service.impl包下

import org.example.fenglish.entity.VocabularyBook;
import org.example.fenglish.entity.WordInBook;
import org.example.fenglish.repository.VocabularyBookRepository;
import org.example.fenglish.repository.WordInBookRepository;
import org.example.fenglish.service.VocabularyBookService;
import org.example.fenglish.vo.request.VocabularyBookAddReq;
import org.example.fenglish.vo.request.VocabularyBookUpdateReq;
import org.example.fenglish.vo.response.VocabularyBookDetailResp;
import org.example.fenglish.vo.response.VocabularyBookSimpleResp;
import org.example.fenglish.vo.response.WordSimpleResp;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

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
    public void addVocabularyBook(VocabularyBookAddReq req) {
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
        book.setPublishTime(req.getPublishTime() == null ? LocalDateTime.now() : req.getPublishTime());
        // 保存到数据库
        vocabularyBookRepository.save(book);
    }

    // 2. 删除单词书（同步删除关联的单词记录）
    @Override
    public void deleteVocabularyBook(String bookId) {
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
    public void updateVocabularyBook(String bookId, VocabularyBookUpdateReq req) {
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
        // 关联查询单词书及包含的单词（使用之前的fetch查询）
        Optional<VocabularyBook> bookOptional = vocabularyBookRepository.findByBookIdWithWords(bookId);
        if (bookOptional.isEmpty()) {
            throw new EntityNotFoundException("单词书不存在");
        }
        VocabularyBook book = bookOptional.get();

        // 实体转VO：手动映射字段，避免关联循环
        VocabularyBookDetailResp resp = new VocabularyBookDetailResp();
        resp.setBookId(book.getBookId());
        resp.setBookName(book.getBookName());
        resp.setPublishTime(book.getPublishTime());

        // 处理单词列表：WordInBook -> WordSimpleResp
        List<WordSimpleResp> wordRespList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(book.getWordInBooks())) {
            for (WordInBook wordInBook : book.getWordInBooks()) {
                WordSimpleResp wordResp = new WordSimpleResp();
                // 从WordInBook关联的EnglishWords中获取单词信息
                wordResp.setWordId(wordInBook.getEnglishWords().getWordId());
                wordResp.setWordName(wordInBook.getEnglishWords().getWordName());
                wordResp.setPartOfSpeech(wordInBook.getEnglishWords().getPartOfSpeech());
                wordResp.setWordExplain(wordInBook.getEnglishWords().getWordExplain());

                wordRespList.add(wordResp);
            }
        }
        resp.setWordList(wordRespList);

        return resp; // 返回VO，而非实体
    }

    @Override
    public List<VocabularyBookSimpleResp> getAllVocabularyBooks() {
        // 1. 查询所有实体类
        List<VocabularyBook> bookEntityList = vocabularyBookRepository.findAll();

        // 2. 新建 VO 列表，用于存储映射后的结果
        List<VocabularyBookSimpleResp> bookVoList = new ArrayList<>();

        // 3. 循环映射：逐个将实体类转换为 VO
        if (!CollectionUtils.isEmpty(bookEntityList)) {
            for (VocabularyBook bookEntity : bookEntityList) {
                VocabularyBookSimpleResp bookVo = new VocabularyBookSimpleResp();
                // 映射字段（实体字段 → VO 字段，一一对应）
                bookVo.setBookId(bookEntity.getBookId());
                bookVo.setBookName(bookEntity.getBookName());
                bookVo.setPublishTime(bookEntity.getPublishTime());
                // 若 VO 有 content 字段，也需映射：bookVo.setContent(bookEntity.getContent());

                // 添加到 VO 列表
                bookVoList.add(bookVo);
            }
        }

        // 4. 返回 VO 列表（而非实体列表）
        return bookVoList;
    }

}
package org.example.fenglish.controller;

import jakarta.validation.Valid;
import org.example.fenglish.entity.VocabularyBook;
import org.example.fenglish.repository.VocabularyBookRepository;
import org.example.fenglish.repository.WordInBookRepository;
import org.example.fenglish.service.VocabularyBookService;
import org.example.fenglish.service.WordInBookService;
import org.example.fenglish.vo.request.VocabularyBookAddReq;
import org.example.fenglish.vo.request.VocabularyBookUpdateReq;
import org.example.fenglish.vo.response.Result;
import org.example.fenglish.vo.response.VocabularyBookSimpleResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 跨域配置：允许前端域名访问
@CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/vocabulary-books")  // 接口前缀
public class VocabularyBookController {

    // 声明为final，lombok会在自动生成的构造器中初始化
    private final VocabularyBookRepository vocabularyBookRepository;
    private final VocabularyBookService vocabularyBookService;
    private final WordInBookService wordInBookService;
    private final WordInBookRepository wordInBookRepository;

    public VocabularyBookController(VocabularyBookService vocabularyBookService,
                                    VocabularyBookRepository vocabularyBookRepository,
                                    WordInBookService wordInBookService,
                                    WordInBookRepository wordInBookRepository, WordInBookRepository wordInBookRepository1) {
        this.vocabularyBookRepository = vocabularyBookRepository;
        this.vocabularyBookService = vocabularyBookService;
        this.wordInBookService = wordInBookService;
        this.wordInBookRepository = wordInBookRepository;
    }


    // 1. 新增单词书（POST）
    @PostMapping
    public Result<Void> addVocabularyBook(@Valid @RequestBody VocabularyBookAddReq req) {
        vocabularyBookService.addVocabularyBook(req);
        return Result.success();
    }

    // 2. 删除单词书（DELETE）
    @DeleteMapping("/{bookId}")
    public Result<Void> deleteVocabularyBook(@PathVariable String bookId) {
        vocabularyBookService.deleteVocabularyBook(bookId);
        return Result.success();
    }

    // 3. 修改单词书（PUT）
    @PutMapping("/{bookId}")
    public Result<Void> updateVocabularyBook(@PathVariable String bookId,
                                             @Valid @RequestBody VocabularyBookUpdateReq req) {
        vocabularyBookService.updateVocabularyBook(bookId, req);
        return Result.success();
    }

    // 4. 查看单词书详情（GET）
    @GetMapping("/{bookId}")
    public Result<?> getVocabularyBookDetail(@PathVariable String bookId) {
        return Result.success(vocabularyBookService.getVocabularyBookDetail(bookId));
    }

    // 5. 查询所有单词书（GET）
    @GetMapping
    public Result<PageResult<VocabularyBookSimpleResp>> getAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 分页参数：Spring Data JPA 的 page 从 0 开始
        Pageable pageable = PageRequest.of(page - 1, size);

        // 执行分页查询
        Page<VocabularyBook> resultPage = vocabularyBookRepository.findAll(pageable);

        // 转换为简单响应对象
        List<VocabularyBookSimpleResp> bookList = resultPage.getContent().stream()
                .map(book -> {
                    VocabularyBookSimpleResp resp = new VocabularyBookSimpleResp();
                    resp.setBookId(book.getBookId());
                    resp.setBookName(book.getBookName());
                    resp.setPublishTime(book.getPublishTime());
                    return resp;
                })
                .collect(Collectors.toList());

        // 封装分页结果
        PageResult<VocabularyBookSimpleResp> pageResult = new PageResult<>(
                bookList,
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                page,
                size
        );

        return Result.success(pageResult);
    }

    // 6. 向单词书添加单词（POST）
    @PostMapping("/{bookId}/words/{wordId}")
    public Result<Void> addWordToBook(@PathVariable String bookId,
                                      @PathVariable String wordId) {
        wordInBookService.addWordToBook(bookId, wordId);
        return Result.success();
    }

    // 7. 从单词书删除单词（DELETE）
    @DeleteMapping("/{bookId}/words/{wordId}")
    public Result<Void> deleteWordFromBook(@PathVariable String bookId,
                                           @PathVariable String wordId) {
        wordInBookService.deleteWordFromBook(bookId, wordId);
        return Result.success();
    }

    // 新增：搜索并分页查询单词书
    @GetMapping("/search")
    public Result<PageResult<VocabularyBookSimpleResp>> searchVocabularyBooks(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size) {

        // 分页参数：Spring Data JPA 的 page 从 0 开始，前端传 1 需减 1
        Pageable pageable = PageRequest.of(page - 1, size);

        // 条件查询：模糊匹配单词书名称（忽略大小写）
        Specification<VocabularyBook> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("bookName")),
                        "%" + name.trim().toLowerCase() + "%"
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 执行分页 + 条件查询
        Page<VocabularyBook> resultPage = vocabularyBookRepository.findAll(spec, pageable);

        // 转换为VO列表
        List<VocabularyBookSimpleResp> voList = resultPage.getContent().stream()
                .map(book -> {
                    VocabularyBookSimpleResp vo = new VocabularyBookSimpleResp();
                    vo.setBookId(book.getBookId());
                    vo.setBookName(book.getBookName());
                    vo.setPublishTime(book.getPublishTime());

                    long wordCount = wordInBookRepository.countByBookId(book.getBookId());
                    vo.setWordCount((int) wordCount);
                    return vo;
                })
                .collect(Collectors.toList());

        // 封装分页结果（使用VO而非实体）
        PageResult<VocabularyBookSimpleResp> pageResult = new PageResult<>(
                voList,                      // 转换后的VO列表
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                page,
                size
        );

        return Result.success(pageResult);
    }
    // 内部类：分页结果封装（前端直接接收）
    public static class PageResult<T> {
        private List<T> list;    // 数据列表
        private long total;      // 总条数
        private int pages;       // 总页数
        private int page;        // 当前页码
        private int size;        // 每页条数

        // 构造器
        public PageResult(List<T> list, long total, int pages, int page, int size) {
            this.list = list;
            this.total = total;
            this.pages = pages;
            this.page = page;
            this.size = size;
        }

        // Getter 方法（必须提供，前端才能解析 JSON 字段）
        public List<T> getList() { return list; }
        public long getTotal() { return total; }
        public int getPages() { return pages; }
        public int getPage() { return page; }
        public int getSize() { return size; }
    }
}
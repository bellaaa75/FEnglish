package org.example.fenglish.controller;

import org.example.fenglish.service.VocabularyBookService;
import org.example.fenglish.service.WordInBookService;
import org.example.fenglish.vo.request.VocabularyBookAddReq;
import org.example.fenglish.vo.request.VocabularyBookUpdateReq;
import org.example.fenglish.vo.response.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 跨域配置：允许前端域名（如http://localhost:8080）访问
@CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/vocabulary-books")  // 接口前缀
@RequiredArgsConstructor
public class VocabularyBookController {

    private final VocabularyBookService vocabularyBookService;
    private final WordInBookService wordInBookService;

    // 1. 新增单词书（POST）
    @PostMapping
    public Result<Void> addVocabularyBook(@Valid @RequestBody VocabularyBookAddReq req,
                                          @RequestHeader("Admin-Id") String adminId) {  // 从请求头获取管理员ID（权限校验）
        vocabularyBookService.addVocabularyBook(req, adminId);
        return Result.success();
    }

    // 2. 删除单词书（DELETE）
    @DeleteMapping("/{bookId}")
    public Result<Void> deleteVocabularyBook(@PathVariable String bookId,
                                             @RequestHeader("Admin-Id") String adminId) {
        vocabularyBookService.deleteVocabularyBook(bookId, adminId);
        return Result.success();
    }

    // 3. 修改单词书（PUT）
    @PutMapping("/{bookId}")
    public Result<Void> updateVocabularyBook(@PathVariable String bookId,
                                             @Valid @RequestBody VocabularyBookUpdateReq req,
                                             @RequestHeader("Admin-Id") String adminId) {
        vocabularyBookService.updateVocabularyBook(bookId, req, adminId);
        return Result.success();
    }

    // 4. 查看单词书详情（GET）
    @GetMapping("/{bookId}")
    public Result<?> getVocabularyBookDetail(@PathVariable String bookId) {
        return Result.success(vocabularyBookService.getVocabularyBookDetail(bookId));
    }

    // 5. 向单词书添加单词（POST）
    @PostMapping("/{bookId}/words/{wordId}")
    public Result<Void> addWordToBook(@PathVariable String bookId,
                                      @PathVariable String wordId,
                                      @RequestHeader("Admin-Id") String adminId) {
        wordInBookService.addWordToBook(bookId, wordId, adminId);
        return Result.success();
    }

    // 6. 从单词书删除单词（DELETE）
    @DeleteMapping("/{bookId}/words/{wordId}")
    public Result<Void> deleteWordFromBook(@PathVariable String bookId,
                                           @PathVariable String wordId,
                                           @RequestHeader("Admin-Id") String adminId) {
        wordInBookService.deleteWordFromBook(bookId, wordId, adminId);
        return Result.success();
    }
}
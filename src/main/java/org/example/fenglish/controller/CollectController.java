package org.example.fenglish.controller;

import org.example.fenglish.common.ApiResponse;
import org.example.fenglish.common.ErrorCodes;
import org.example.fenglish.common.exception.BusinessException;
import org.example.fenglish.repository.CollectBookProjection;
import org.example.fenglish.repository.CollectWordProjection;
import org.example.fenglish.vo.dto.WordDTO;
import org.example.fenglish.service.CollectService;
import lombok.RequiredArgsConstructor;
import org.example.fenglish.vo.response.CollectVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
public class CollectController {

    private final CollectService service;

    /* 收藏单词 */
    @PostMapping("/word/{wordId}")
    public ApiResponse<Void> collectWord(@PathVariable String wordId,
                                         @RequestHeader("userId") String userId) {
        service.collect(userId, wordId, (byte) 1);
        return ApiResponse.success();
    }

    /* 收藏单词书 */
    @PostMapping("/book/{bookId}")
    public ApiResponse<Void> collectBook(@PathVariable String bookId,
                                         @RequestHeader("userId") String userId) {
        service.collect(userId, bookId, (byte) 2);
        return ApiResponse.success();
    }

    /* 取消收藏单词 */
    @DeleteMapping("/word/{wordId}")
    public ApiResponse<Void> unCollectWord(@PathVariable String wordId,
                                           @RequestHeader("userId") String userId) {
        service.unCollect(userId, wordId, (byte) 1);
        return ApiResponse.success();
    }

    /* 取消收藏单词书 */
    @DeleteMapping("/book/{bookId}")
    public ApiResponse<Void> unCollectBook(@PathVariable String bookId,
                                           @RequestHeader("userId") String userId) {
        service.unCollect(userId, bookId, (byte) 2);
        return ApiResponse.success();
    }

    /* 我的单词收藏 */
    @GetMapping("/words")
    public ApiResponse<Page<CollectWordProjection>> myWords(
            @PageableDefault(size = 10) Pageable p,
            @RequestHeader("userId") String userId) {
        return ApiResponse.success(service.myWordCollects(userId, p));
    }

    /* 我的单词书收藏 */
    @GetMapping("/books")
    public ApiResponse<Page<CollectBookProjection>> myBooks(
            @PageableDefault(size = 10) Pageable p,
            @RequestHeader("userId") String userId) {
        return ApiResponse.success(service.myBookCollects(userId, p));
    }
}
package org.example.fenglish.controller;

import org.example.fenglish.common.ApiResponse;
import org.example.fenglish.common.ErrorCodes;
import org.example.fenglish.common.exception.BusinessException;
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

    /**
     * 收藏单词
     */
    @PostMapping("/{wordId}")
    public ApiResponse<Void> collect(@PathVariable String wordId,
                                     @RequestHeader("user-id") String userId) {
        service.collectWord(userId, wordId);   // 里面抛业务异常即可
        return ApiResponse.success();          // 成功统一 200
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{wordId}")
    public ApiResponse<Void> unCollect(@PathVariable String wordId,
                                       @RequestHeader("user-id") String userId) {
        service.unCollectWord(userId, wordId);
        return ApiResponse.success();
    }

    /**
     * 我的收藏列表
     */
    @GetMapping
    public ApiResponse<CollectVO> myCollects(
            @PageableDefault(size = 10) Pageable page,
            @RequestHeader("user-id") String userId) {

        CollectVO vo = service.myCollects(userId, page);
        return ApiResponse.success(vo);
    }
}
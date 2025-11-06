package org.example.fenglish.service;

import org.example.fenglish.vo.dto.WordDTO;
import org.example.fenglish.vo.response.CollectVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 收藏业务接口
 */
public interface CollectService {


    /** 收藏单词；失败抛 BusinessException */
    void collectWord(String userId, String wordId);

    /** 取消收藏；找不到记录抛 BusinessException */
    void unCollectWord(String userId, String wordId);

    /** 我的收藏列表；不可能为空直接返回 Page */
    CollectVO myCollects(String userId, Pageable pageable);

    /**
     * 收藏单词
     * @param userId 当前登录用户ID
     * @param wordId 单词ID
     */
//    boolean collectWord(String userId, String wordId);
    /**
     * 取消收藏
     * @param userId 当前登录用户ID
     * @param wordId 单词ID
     */
//    boolean unCollectWord(String userId, String wordId);

    /**
     * 我的收藏列表（含真实单词信息）
     * @param userId 当前登录用户ID
     * @param pageable 分页参数
     * @return 单词传输对象分页
     */
//    Page<WordDTO> myCollects(String userId, Pageable pageable);
}
package org.example.fenglish.service;

import org.example.fenglish.repository.CollectBookProjection;
import org.example.fenglish.repository.CollectWordProjection;
import org.example.fenglish.vo.dto.WordDTO;
import org.example.fenglish.vo.response.CollectVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 收藏业务接口
 */
public interface CollectService {


    /** 收藏：type=1 单词，2 单词书 */
    void collect(String userId, String targetId, Byte  targetType);

    /** 取消收藏 */
    void unCollect(String userId, String targetId, Byte  targetType);

    /** 我的收藏（单词） */
    Page<CollectWordProjection> myWordCollects(String userId, Pageable p);

    /** 我的收藏（单词书） */
    Page<CollectBookProjection> myBookCollects(String userId, Pageable p);

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
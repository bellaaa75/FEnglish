package org.example.fenglish.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fenglish.repository.*;
import org.example.fenglish.vo.dto.WordDTO;
import org.example.fenglish.entity.Collect;
import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.entity.User;
import org.example.fenglish.service.CollectService;
import org.example.fenglish.vo.response.CollectVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.example.fenglish.common.ErrorCodes;
import org.example.fenglish.common.exception.BusinessException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectServiceImpl implements CollectService {

    private final CollectRepository repo;
    private final EnglishWordsRepository wordRepo;
    private final VocabularyBookRepository bookRepo;

    @Override
    @Transactional
    public void collect(String userId, String targetId, Byte type) {
        // 1. 去重
        if (repo.existsByUserIdAndTargetIdAndTargetType(userId, targetId, type)) {
            throw new BusinessException(ErrorCodes.ALREADY_COLLECTED);
        }
        // 2. 存在性校验
        if (type == 1 && !wordRepo.existsById(targetId)) {
            throw new BusinessException(ErrorCodes.WORD_NOT_FOUND);
        }
        if (type == 2 && !bookRepo.existsById(targetId)) {
            throw new BusinessException(ErrorCodes.BOOK_NOT_FOUND);
        }
        // 3. 上限（可选）
        if (repo.countByUserId(userId) >= 2000) {
            throw new BusinessException(ErrorCodes.COLLECT_LIMIT_REACHED);
        }
        // 4. 保存
        Collect c = new Collect();
        c.setCollectId(UUID.randomUUID().toString());
        c.setUserId(userId);
        c.setTargetId(targetId);
        c.setTargetType(type);
        c.setCollectTime(new Date());
        repo.save(c);
    }

    @Override
    @Transactional
    public void unCollect(String userId, String targetId, Byte type) {
        int rows = repo.deleteByUserIdAndTargetIdAndTargetType(userId, targetId, type);
        if (rows == 0) throw new BusinessException(ErrorCodes.COLLECT_NOT_FOUND);
    }

    /* 下面两个查询直接返回投影，Controller 里再组装 VO */
    @Override
    public Page<CollectWordProjection> myWordCollects(String userId, Pageable p) {
        return repo.findWordCollects(userId, p);
    }

    @Override
    public Page<CollectBookProjection> myBookCollects(String userId, Pageable p) {
        return repo.findBookCollects(userId, p);
    }
}
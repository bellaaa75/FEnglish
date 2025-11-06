package org.example.fenglish.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fenglish.vo.dto.WordDTO;
import org.example.fenglish.entity.Collect;
import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.entity.User;
import org.example.fenglish.repository.CollectRepository;
import org.example.fenglish.repository.EnglishWordsRepository;
import org.example.fenglish.repository.UserRepository;
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

    private final CollectRepository collectRepository;
    private final EnglishWordsRepository wordRepository;
    private final UserRepository userRepository;

    /* ---------- 1. 收藏 ---------- */
    @Override
    @Transactional
    public void collectWord(String userId, String wordId) {
        // 1. 重复收藏
        if (collectRepository.existsByEnglishWord_WordIdAndUser_UserId(wordId, userId)) {
            throw new BusinessException(ErrorCodes.ALREADY_COLLECTED);
        }
        // 2. 单词存在性
        EnglishWords word = wordRepository.findById(wordId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.WORD_NOT_FOUND));
        // 3. 用户存在性
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodes.USER_NOT_FOUND);
        }

//         4. 收藏上限（可选）
        long count = collectRepository.countByUser_UserId(userId);
        if (count >= 2_000) {          // 例：2000 条上限
            throw new BusinessException(ErrorCodes.COLLECT_LIMIT_REACHED);
        }

        // 5. 保存
        Collect collect = new Collect();
        collect.setCollectId("COL_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        collect.setEnglishWord(word);
        collect.setUser(user);
        collect.setCollectTime(new Date());
        collectRepository.save(collect);
    }

    /* ---------- 2. 取消收藏 ---------- */
    @Override
    @Transactional
    public void unCollectWord(String userId, String wordId) {
        int rows = collectRepository.deleteByEnglishWord_WordIdAndUser_UserId(wordId, userId);
        if (rows == 0) {
            throw new BusinessException(ErrorCodes.COLLECT_NOT_FOUND);
        }
    }

    /* ---------- 3. 我的收藏 ---------- */
    @Override
    public CollectVO myCollects(String userId, Pageable pageable) {
        Page<Collect> page = collectRepository.findByUser_UserId(userId, pageable);

        List<CollectVO.CollectItem> items = page.getContent()
                .stream()
                .map(c -> {
                    EnglishWords w = c.getEnglishWord();
                    CollectVO.CollectItem item = new CollectVO.CollectItem();
                    item.setWordId(w.getWordID());
                    item.setWordName(w.getWordName());
                    item.setWordExplain(w.getWordExplain());
                    item.setCollectTime(c.getCollectTime());
                    return item;
                })
                .toList();

        CollectVO vo = new CollectVO();
        vo.setTotal(page.getTotalElements());
        vo.setItems(items);
        return vo;
    }
}
package org.example.fenglish.service.impl;

import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.repository.EnglishWordsRepository;
import org.example.fenglish.service.EnglishWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnglishWordsServiceImpl implements EnglishWordsService {

    @Autowired
    private EnglishWordsRepository englishWordsRepository;

    @Override
    public boolean addWords(EnglishWords word) {
        try {
            // 生成WordID（按报告要求使用varchar(50)）
            if (word.getWordID() == null) {
                word.setWordID("WORD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            }
            englishWordsRepository.save(word);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteWords(String wordId) {
        try {
            if (englishWordsRepository.existsById(wordId)) {
                englishWordsRepository.deleteById(wordId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean editWords(String wordId, EnglishWords word) {
        try {
            EnglishWords existingWord = getWordById(wordId);
            if (existingWord != null) {
                // 使用实体类的setter方法（按照需求报告）
                existingWord.setWordName(word.getWordName());
                existingWord.setWordExplain(word.getWordExplain());
                existingWord.setPartOfSpeech(word.getPartOfSpeech());
                existingWord.setThirdPersonSingular(word.getThirdPersonSingular());
                existingWord.setPresentParticiple(word.getPresentParticiple());
                existingWord.setPastParticiple(word.getPastParticiple());
                existingWord.setPastTense(word.getPastTense());
                existingWord.setExampleSentence(word.getExampleSentence());

                englishWordsRepository.save(existingWord);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public EnglishWords getWordById(String wordId) {
        return englishWordsRepository.findById(wordId).orElse(null);
    }

    @Override
    public EnglishWords getWordByName(String wordName) {
        return englishWordsRepository.findByWordName(wordName);
    }

    @Override
    public List<EnglishWords> getWordsByFuzzyName(String wordName) {
        return englishWordsRepository.findByWordNameContainingIgnoreCase(wordName);
    }

    @Override
    public Page<EnglishWords> getWordList(int pageNum, int pageSize) {
        // 注意：Spring Data JPA的页码是从0开始的
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("wordName").ascending());
        return englishWordsRepository.findAll(pageable);
    }
}
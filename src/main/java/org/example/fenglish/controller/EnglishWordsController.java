package org.example.fenglish.controller;

import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.service.EnglishWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
public class EnglishWordsController {

    @Autowired
    private EnglishWordsService englishWordsService;

    // 增加单词 - 严格按照需求报告：addWords() -> bool
    @PostMapping
    public ResponseEntity<Map<String, Object>> addWords(@RequestBody EnglishWords word) {
        Map<String, Object> response = new HashMap<>();
        boolean success = englishWordsService.addWords(word);
        response.put("success", success);
        response.put("message", success ? "单词添加成功" : "单词添加失败");
        if (success) {
            response.put("wordId", word.getWordID());
        }
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // 删除单词 - 严格按照需求报告：deleteWords() -> bool
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Map<String, Object>> deleteWords(@PathVariable String wordId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = englishWordsService.deleteWords(wordId);
        response.put("success", success);
        response.put("message", success ? "单词删除成功" : "单词删除失败，可能单词不存在");
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // 修改单词 - 严格按照需求报告：editWords() -> bool
    @PutMapping("/{wordId}")
    public ResponseEntity<Map<String, Object>> editWords(@PathVariable String wordId, @RequestBody EnglishWords word) {
        Map<String, Object> response = new HashMap<>();
        boolean success = englishWordsService.editWords(wordId, word);
        response.put("success", success);
        response.put("message", success ? "单词修改成功" : "单词修改失败，可能单词不存在");
        return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }


    // 根据id获取单词
    @GetMapping("/{wordId}")
    public ResponseEntity<EnglishWords> getWordById(@PathVariable String wordId) {
        EnglishWords word = englishWordsService.getWordById(wordId);
        return word != null ? ResponseEntity.ok(word) : ResponseEntity.notFound().build();
    }

    // 根据名称获取单词
    @GetMapping("/name/{wordName}")
    public ResponseEntity<EnglishWords> getWordByName(@PathVariable String wordName) {
        EnglishWords word = englishWordsService.getWordByName(wordName);
        return word != null ? ResponseEntity.ok(word) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name/fuzzy/{wordName}")
    public ResponseEntity<List<EnglishWords>> getWordsByFuzzyName(@PathVariable String wordName) {
        List<EnglishWords> words = englishWordsService.getWordsByFuzzyName(wordName);
        // 若查询结果为空，仍返回200+空列表（更友好，前端可提示“无匹配单词”）
        return ResponseEntity.ok(words);
    }
}
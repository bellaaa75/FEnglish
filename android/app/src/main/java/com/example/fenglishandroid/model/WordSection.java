package com.example.fenglishandroid.model;

import java.util.*;

/**
 * 一级 = 一个日期 + 当天所有单词
 */
public class WordSection {
    public String date;                 // 2025-11-24
    public List<CollectWordDTO> words;  // 当天单词（targetId 非空）
}
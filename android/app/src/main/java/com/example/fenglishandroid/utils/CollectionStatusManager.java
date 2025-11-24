package com.example.fenglishandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class CollectionStatusManager {
    //  SharedPreferences 文件名（自定义，建议唯一）
    private static final String PREF_NAME = "collection_status";
    // 存储收藏单词书ID的key
    private static final String KEY_COLLECTED_BOOKS = "collected_books";

    //  SharedPreferences 实例
    private SharedPreferences preferences;
    // 内存中缓存的收藏ID集合（避免频繁读写SP）
    private Set<String> collectedBookIds;

    /**
     * 构造方法（需传入Context初始化SP）
     * @param context 上下文（建议用Application Context，避免内存泄漏）
     */
    public CollectionStatusManager(Context context) {
        // 初始化SP：模式用MODE_PRIVATE（仅本应用可访问）
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // 从SP读取收藏ID集合，无数据则返回空HashSet
        collectedBookIds = new HashSet<>(preferences.getStringSet(KEY_COLLECTED_BOOKS, new HashSet<>()));
    }

    /**
     * 获取所有收藏的单词书ID
     * @return 不可变集合（避免外部直接修改）
     */
    public Set<String> getCollectedBookIds() {
        return new HashSet<>(collectedBookIds); // 返回副本，防止原集合被篡改
    }

    /**
     * 添加收藏（内存+SP双重存储）
     * @param bookId 单词书ID
     */
    public void addCollectedBook(String bookId) {
        if (bookId != null && !bookId.isEmpty()) {
            collectedBookIds.add(bookId);
            saveCollectedBooks(); // 同步到SP
        }
    }

    /**
     * 取消收藏（内存+SP双重删除）
     * @param bookId 单词书ID
     */
    public void removeCollectedBook(String bookId) {
        if (bookId != null) {
            collectedBookIds.remove(bookId);
            saveCollectedBooks(); // 同步到SP
        }
    }

    /**
     * 检查某个单词书是否已收藏
     * @param bookId 单词书ID
     * @return true=已收藏，false=未收藏
     */
    public boolean isBookCollected(String bookId) {
        return bookId != null && collectedBookIds.contains(bookId);
    }

    /**
     * 清空所有收藏（谨慎使用）
     */
    public void clearAllCollections() {
        collectedBookIds.clear();
        saveCollectedBooks();
    }

    /**
     * 内部方法：将内存中的收藏集合同步到SharedPreferences
     */
    private void saveCollectedBooks() {
        preferences.edit()
                .putStringSet(KEY_COLLECTED_BOOKS, collectedBookIds)
                .apply(); // 异步提交（比commit()高效，无返回值）
    }
}
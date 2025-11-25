package com.example.fenglishandroid.viewModel;

import android.util.Log;

import androidx.lifecycle.*;
import com.example.fenglishandroid.model.CollectWordDTO;
import com.example.fenglishandroid.model.CollectBookDTO;
import com.example.fenglishandroid.model.WordSection;
import com.example.fenglishandroid.repository.CollectRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectViewModel extends ViewModel {
    private final CollectRepository repo = new CollectRepository();

    private final Set<String> collectedWordIds = new HashSet<>();
    private final Set<String> collectedBookIds = new HashSet<>();

    /* -------- 单词收藏 -------- */
    private final MutableLiveData<List<WordSection>> wordSectionLive = new MutableLiveData<>();
    private final MutableLiveData<String> wordError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> collectResult = new MutableLiveData<>();

    public LiveData<List<WordSection>> getWordSections() { return wordSectionLive; }
    public LiveData<String> getWordError() { return wordError; }

    // 添加获取收藏状态的方法
    public boolean isWordCollected(String wordId) {
        return collectedWordIds.contains(wordId);
    }

    public boolean isBookCollected(String bookId) {
        return collectedBookIds.contains(bookId);
    }

    public MutableLiveData<Boolean> getCollectResult() {
        return collectResult;
    }
    // 在 CollectViewModel 类中添加以下方法：

    /**
     * 更新单词收藏状态
     */
    public void updateWordCollectionStatus(String wordId, boolean collected) {
        if (collected) {
            collectedWordIds.add(wordId);
        } else {
            collectedWordIds.remove(wordId);
        }
    }

    /**
     * 批量更新单词收藏状态（用于初始化）
     */
    public void updateWordCollectionStatus(Set<String> wordIds) {
        collectedWordIds.clear();
        collectedWordIds.addAll(wordIds);
    }

    /**
     * 获取所有已收藏的单词ID
     */
    public Set<String> getCollectedWordIds() {
        return new HashSet<>(collectedWordIds);
    }

    public void loadWordCollects(int page, int size) {
        repo.loadWordCollects(page, size, wordSectionLive, wordError);
    }

    // 在 CollectViewModel 中，这两个方法已经正确实现了列表刷新
    public void collectWord(String wordId) {
        repo.collectWord(wordId,
                () -> {
                    Log.d("CollectViewModel", "收藏单词成功: " + wordId);
                    collectedWordIds.add(wordId);
                    collectResult.postValue(true);
                    // 确保重新加载单词收藏列表
                    Log.d("CollectViewModel", "🔄 收藏成功后重新加载列表");
                    loadWordCollects(0, 20); // 这里已经刷新了列表
                    if (collectionStatusListener != null) {
                        collectionStatusListener.onCollectionStatusChanged(wordId, true);
                    }
                },
                () -> {
                    Log.e("CollectViewModel", "收藏单词失败: " + wordId);
                    collectResult.postValue(false);
                    wordError.setValue("收藏失败");
                });
    }

    public void unCollectWord(String wordId) {
        repo.unCollectWord(wordId,
                () -> {
                    Log.d("CollectViewModel", "取消收藏单词成功: " + wordId);
                    collectedWordIds.remove(wordId);
                    collectResult.postValue(true);
                    loadWordCollects(0, 20); // 这里已经刷新了列表
                    if (collectionStatusListener != null) {
                        collectionStatusListener.onCollectionStatusChanged(wordId, false);
                    }
                },
                () -> {
                    Log.e("CollectViewModel", "取消收藏单词失败: " + wordId);
                    collectResult.postValue(false);
                    wordError.setValue("取消收藏失败");
                    loadWordCollects(0, 20); // 即使失败也刷新
                });
    }


    /* -------- 单词书收藏 -------- */
    private final MutableLiveData<List<CollectBookDTO>> bookLive = new MutableLiveData<>();
    private final MutableLiveData<String> bookError = new MutableLiveData<>();

    public LiveData<List<CollectBookDTO>> getBookCollects() { return bookLive; }
    public LiveData<String> getBookError() { return bookError; }

    public void loadBookCollects(int page, int size) {
        repo.loadBookCollects(page, size, bookLive, bookError);

        // 添加日志，确认 LiveData 是否正确设置
        bookLive.observeForever(data -> {
            if (data != null) {
                for (CollectBookDTO book : data) {
                    Log.d("CollectViewModel", "LiveData 中的收藏时间：" + book.getCollectTime());
                }
            }
        });
    }

    // 在 collectBook 和 unCollectBook 方法中调用监听器
    public void collectBook(String bookId) {
        repo.collectBook(bookId,
                () -> {
                    loadBookCollects(0, 10);
                    if (collectionStatusListener != null) {
                        collectionStatusListener.onCollectionStatusChanged(bookId, true);
                    }
                },
                () -> bookError.setValue("收藏失败"));
    }

    public void unCollectBook(String bookId) {
        repo.unCollectBook(bookId,
                () -> {
                    loadBookCollects(0, 10);
                    if (collectionStatusListener != null) {
                        collectionStatusListener.onCollectionStatusChanged(bookId, false);
                    }
                },
                () -> {
                    bookError.setValue("取消收藏失败");
                    loadBookCollects(0, 10);
                });
    }


    // 定义收藏状态变化监听器
    public interface CollectionStatusListener {
        void onCollectionStatusChanged(String bookId, boolean isCollected);
    }

    private CollectionStatusListener collectionStatusListener;

    public void setCollectionStatusListener(CollectionStatusListener listener) {
        this.collectionStatusListener = listener;
    }
    /**
     * 供学习广场调用：把一本新书立即插到列表头部，让 CollectBookFragment 立刻可见
     */
    public void addBookToList(CollectBookDTO newBook) {
        List<CollectBookDTO> curr = bookLive.getValue();
        if (curr == null) curr = new ArrayList<>();
        List<CollectBookDTO> copy = new ArrayList<>(curr);
        copy.add(0, newBook);   // 插到最前面
        bookLive.postValue(copy); // 立即刷新 UI
    }
}
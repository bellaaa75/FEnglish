package com.example.fenglishandroid.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.repository.VocabularyBookRepository;

import java.util.List;

public class VocabularyBookViewModel extends AndroidViewModel {
    private VocabularyBookRepository repository;
    private MutableLiveData<List<VocabularyBookSimpleResp>> bookListLiveData;
    private MutableLiveData<Boolean> loadingLiveData;
    private MutableLiveData<String> errorLiveData;

    public VocabularyBookViewModel(@NonNull Application application) {
        super(application);
        repository = new VocabularyBookRepository();
        bookListLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    // 获取单词书列表
    public void loadBookList(int page, int size) {
        loadingLiveData.setValue(true);
        repository.getBookList(page, size, new VocabularyBookRepository.RepositoryCallback<PageResult<VocabularyBookSimpleResp>>() {
            @Override
            public void onSuccess(PageResult<VocabularyBookSimpleResp> data) {
                loadingLiveData.setValue(false);
                bookListLiveData.setValue(data.getList());
            }

            @Override
            public void onFailure(Throwable throwable) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(throwable.getMessage());
            }
        });
    }

    // 搜索单词书
    public void searchBooks(String keyword, int page, int size) {
        repository.searchBooks(keyword, page, size, new VocabularyBookRepository.RepositoryCallback<PageResult<VocabularyBookSimpleResp>>() {
            @Override
            public void onSuccess(PageResult<VocabularyBookSimpleResp> data) {
                bookListLiveData.postValue(data.getList());
            }

            @Override
            public void onFailure(Throwable throwable) {
                // 处理错误
            }
        });
    }

    // getter方法
    public MutableLiveData<List<VocabularyBookSimpleResp>> getBookListLiveData() { return bookListLiveData; }
    public MutableLiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
}
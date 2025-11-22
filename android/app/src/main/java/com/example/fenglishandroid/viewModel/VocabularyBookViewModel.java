package com.example.fenglishandroid.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.model.request.VocabularyBookAddReq;
import com.example.fenglishandroid.model.request.VocabularyBookUpdateReq;
import com.example.fenglishandroid.repository.VocabularyBookRepository;

import java.util.List;

public class VocabularyBookViewModel extends AndroidViewModel {
    private VocabularyBookRepository repository;
    private MutableLiveData<List<VocabularyBookSimpleResp>> bookListLiveData;
    private MutableLiveData<Boolean> loadingLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> addResultLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateResultLiveData = new MutableLiveData<>();
    private MutableLiveData<VocabularyBookDetailResp> bookDetailLiveData = new MutableLiveData<>();

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
                errorLiveData.postValue(throwable.getMessage());
            }
        });
    }

    // 新增单词书
    public void addVocabularyBook(VocabularyBookAddReq req) {
        repository.addBook(req, new VocabularyBookRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                addResultLiveData.postValue(true);
            }

            @Override
            public void onFailure(Throwable throwable) {
                addResultLiveData.postValue(false);
                errorLiveData.postValue(throwable.getMessage());
            }
        });
    }

    // 获取单词书详情
    public LiveData<VocabularyBookDetailResp> getBookDetail(String bookId) {
        repository.getBookDetail(bookId, new VocabularyBookRepository.RepositoryCallback<VocabularyBookDetailResp>() {
            @Override
            public void onSuccess(VocabularyBookDetailResp data) {
                bookDetailLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable throwable) {
                bookDetailLiveData.postValue(null);
                errorLiveData.postValue(throwable.getMessage());
            }
        });
        return bookDetailLiveData;
    }

    // 更新单词书
    public void updateVocabularyBook(String bookId, VocabularyBookUpdateReq req) {
        repository.updateBook(bookId, req, new VocabularyBookRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                updateResultLiveData.postValue(true);
            }

            @Override
            public void onFailure(Throwable throwable) {
                updateResultLiveData.postValue(false);
                errorLiveData.postValue(throwable.getMessage());
            }
        });
    }

    // getter方法
    public MutableLiveData<List<VocabularyBookSimpleResp>> getBookListLiveData() { return bookListLiveData; }
    public MutableLiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getAddResultLiveData() { return addResultLiveData; }
    public LiveData<Boolean> getUpdateResultLiveData() { return updateResultLiveData; }
}
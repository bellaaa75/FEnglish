package com.example.fenglishandroid.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.repository.WordRepository;
import retrofit2.Call;

public class WordViewModel extends AndroidViewModel {
    private final WordRepository wordRepository;
    private final MutableLiveData<Result<PageResult<WordSimpleResp>>> wordPageResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    // 添加共享的 CollectViewModel 引用
    private CollectViewModel sharedCollectViewModel;
    private final MutableLiveData<Boolean> collectResult = new MutableLiveData<>();

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository();
    }

    public void setSharedCollectViewModel(CollectViewModel viewModel) {
        this.sharedCollectViewModel = viewModel;
    }

    public MutableLiveData<Result<PageResult<WordSimpleResp>>> getWordPageResult() {
        return wordPageResult;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getCollectResult() {
        return collectResult;
    }

    /**
     * 根据单词书ID分页获取单词列表
     * @param bookId 单词书ID
     * @param page 页码
     * @param size 每页数量
     */
    public void getWordsByBookId(String bookId, int page, int size) {
        wordRepository.getWordsByBookId(bookId, page, size, new WordRepository.RepositoryCallback<PageResult<WordSimpleResp>>() {
            @Override
            public void onSuccess(PageResult<WordSimpleResp> data) {
                Result<PageResult<WordSimpleResp>> result = new Result<>();
                result.setCode(200);
                result.setSuccess(true);
                result.setData(data);
                wordPageResult.postValue(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // 构建错误Result对象
                Result<PageResult<WordSimpleResp>> result = new Result<>();
                result.setCode(500);
                result.setSuccess(false);
                result.setMessage(throwable.getMessage());
                wordPageResult.postValue(result);
            }
        });
    }
    /**
     * 收藏单词
     * @param wordId 单词ID
     */
    public void collectWord(String wordId) {
        if (sharedCollectViewModel != null) {
            sharedCollectViewModel.collectWord(wordId);
            collectResult.postValue(true);
        } else {
            errorLiveData.postValue("收藏功能未初始化");
        }
    }
}
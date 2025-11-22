package com.example.fenglishandroid.ui.admin;

import static com.example.fenglishandroid.ui.admin.AddVocabularyBookActivity.FORMATTER;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.request.VocabularyBookUpdateReq;
import com.example.fenglishandroid.viewModel.VocabularyBookViewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditVocabularyBookActivity extends AppCompatActivity {
    private EditText etBookId, etBookName, etPublishTime;
    private Calendar calendar;
    private VocabularyBookViewModel viewModel;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vocabulary_book);

        // 获取传递的bookId
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null || bookId.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化控件
        etBookId = findViewById(R.id.et_book_id);
        etBookName = findViewById(R.id.et_book_name);
        etPublishTime = findViewById(R.id.et_publish_time);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnSave = findViewById(R.id.btn_save);
        calendar = Calendar.getInstance();
        viewModel = new ViewModelProvider(this).get(VocabularyBookViewModel.class);

        // 加载单词书详情
        loadBookDetail();

        // 时间选择器
        etPublishTime.setOnClickListener(v -> showDateTimePicker());

        // 取消按钮
        btnCancel.setOnClickListener(v -> finish());

        // 保存按钮
        btnSave.setOnClickListener(v -> saveChanges());

        // 观察更新结果
        viewModel.getUpdateResultLiveData().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "更新失败，请检查输入", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 加载单词书详情
    private void loadBookDetail() {
        viewModel.getBookDetail(bookId).observe(this, new Observer<VocabularyBookDetailResp>() {
            @Override
            public void onChanged(VocabularyBookDetailResp book) {
                if (book != null) {
                    etBookId.setText(book.getBookId());
                    etBookName.setText(book.getBookName());
                    etPublishTime.setText(book.getPublishTime());

                    // 解析时间到日历（便于编辑）
                    if (book.getPublishTime() != null && !book.getPublishTime().isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                            Date date = sdf.parse(book.getPublishTime());
                            if (date != null) {
                                calendar.setTime(date);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(EditVocabularyBookActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    // 显示日期时间选择器
    private void showDateTimePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // 时间选择
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // 格式化时间并显示
                LocalDateTime dateTime = LocalDateTime.of(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute
                );
                etPublishTime.setText(FORMATTER.format(dateTime));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    // 保存修改
    private void saveChanges() {
        String bookName = etBookName.getText().toString().trim();
        String publishTimeStr = etPublishTime.getText().toString().trim();

        // 验证名称必填
        if (bookName.isEmpty()) {
            etBookName.setError("单词书名称不能为空");
            etBookName.requestFocus();
            return;
        }

        // 构建更新请求
        VocabularyBookUpdateReq req = new VocabularyBookUpdateReq();
        req.setBookName(bookName);
        // 将字符串转换为LocalDateTime
        try {
            if (!publishTimeStr.isEmpty()) {
                req.setPublishTime(publishTimeStr);
            } else {
                req.setPublishTime(null);
            }
        } catch (Exception e) {
            etPublishTime.setError("时间格式错误（正确格式：yyyy-MM-dd'T'HH:mm:ss）");
            etPublishTime.requestFocus();
            return;
        }

        // 调用ViewModel更新方法
        viewModel.updateVocabularyBook(bookId, req);
    }
}
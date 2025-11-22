package com.example.fenglishandroid.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.VocabularyBookAddReq;
import com.example.fenglishandroid.viewModel.VocabularyBookViewModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class AddVocabularyBookActivity extends AppCompatActivity {
    private EditText etBookId, etBookName, etPublishTime;
    private Calendar calendar;
    private VocabularyBookViewModel viewModel;

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocabulary_book);

        // 初始化控件
        etBookId = findViewById(R.id.et_book_id);
        etBookName = findViewById(R.id.et_book_name);
        etPublishTime = findViewById(R.id.et_publish_time);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnSubmit = findViewById(R.id.btn_submit);
        calendar = Calendar.getInstance();
        viewModel = new ViewModelProvider(this).get(VocabularyBookViewModel.class);

        // 时间选择器
        etPublishTime.setOnClickListener(v -> showDateTimePicker());

        // 取消按钮
        btnCancel.setOnClickListener(v -> finish());

        // 提交按钮
        btnSubmit.setOnClickListener(v -> submitForm());

        // 观察添加结果
        viewModel.getAddResultLiveData().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "新增失败，请检查输入", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 显示日期时间选择器
    private void showDateTimePicker() {
        // 日期选择
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // 时间选择
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                // 格式化显示（与后端保持一致：YYYY-MM-DDTHH:mm:ss）
                String formattedTime = FORMATTER.format(
                        LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                );
                etPublishTime.setText(formattedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // 表单提交验证
    private void submitForm() {
        String bookId = etBookId.getText().toString().trim();
        String bookName = etBookName.getText().toString().trim();
        String publishTimeStr = etPublishTime.getText().toString().trim();

        // 验证名称必填
        if (bookName.isEmpty()) {
            etBookName.setError("单词书名称不能为空");
            etBookName.requestFocus();
            return;
        }

        // 构建请求参数
        VocabularyBookAddReq req = new VocabularyBookAddReq();
        req.setBookId(bookId.isEmpty() ? null : bookId);
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

        // 调用ViewModel添加方法
        viewModel.addVocabularyBook(req);
    }
}
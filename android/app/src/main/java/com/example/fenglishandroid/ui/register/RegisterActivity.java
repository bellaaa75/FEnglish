package com.example.fenglishandroid.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;
import com.example.fenglishandroid.ui.login.LoginActivity;
import com.example.fenglishandroid.viewModel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUserName, etUserPassword, etConfirmPassword, etPhoneNumber, etUserMailbox;
    private Spinner spinnerGender;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();
        setupClickListeners();
    }

    private void initViews() {
        etUserName = findViewById(R.id.et_user_name);
        etUserPassword = findViewById(R.id.et_user_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etUserMailbox = findViewById(R.id.et_user_mailbox);
        //spinnerGender = findViewById(R.id.spinner_gender);
        btnRegister = findViewById(R.id.btn_register);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        // 设置性别下拉框
        //setupGenderSpinner();
    }

    /*private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }*/

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getRegisterLiveData().observe(this, response -> {
            if (response.isSuccess()) {
                Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
                // 注册成功后跳转到登录界面
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> doRegister());

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void doRegister() {
        String userName = etUserName.getText().toString().trim();
        String userPassword = etUserPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String userMailbox = etUserMailbox.getText().toString().trim();
        //String gender = spinnerGender.getSelectedItem().toString();


        // 基本验证
        if (userMailbox.isEmpty() || userName.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写必填信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if ("请选择性别".equals(gender)) {
            gender = ""; // 或者设置为默认值
        }*/

        OrdinaryUserRegisterRequest request = new OrdinaryUserRegisterRequest();
        request.setUserName(userName);
        request.setUserPassword(userPassword);
        if (!phoneNumber.isEmpty()) {
            request.setPhoneNumber(phoneNumber);
        }
        request.setUserMailbox(userMailbox);
        /*request.setGender(gender);*/

        userViewModel.registerOrdinary(request);
    }
}
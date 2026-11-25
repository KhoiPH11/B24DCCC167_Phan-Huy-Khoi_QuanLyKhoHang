package com.example.InventoryManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private TextView tvError;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        tvError.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> checkLogin());
    }

    private void checkLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra bỏ trống
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // Kiểm tra với database
        if (dbHelper.checkLogin(username, password)) {
            tvError.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Không cho Back quay lại Login
        } else {
            tvError.setText("Sai tên đăng nhập hoặc mật khẩu");
            tvError.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}

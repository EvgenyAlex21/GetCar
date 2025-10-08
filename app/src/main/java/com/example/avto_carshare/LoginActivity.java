package com.example.avto_carshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.User;
import com.example.avto_carshare.utils.SessionManager;
import com.example.avto_carshare.utils.ValidationUtils;

// Активность для входа пользователя в приложение
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
            dbHelper = new DatabaseHelper(this);
            sessionManager = new SessionManager(this);
            if (sessionManager.isLoggedIn()) {
                navigateToMain();
                return;
            }
            initViews();
            setupListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            loginUser();
            return true;
        });
    }

    // Обработка входа пользователя
    private void loginUser() {
        try {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (!validateInput(email, password)) {
                return;
            }
            setLoading(true);
            User user = dbHelper.loginUser(email, password);
            setLoading(false);
            if (user != null) {
                sessionManager.createLoginSession(user.getId(), user.getEmail(), user.getFullName());
                Toast.makeText(this, "Добро пожаловать, " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
                etPassword.requestFocus();
            }
        } catch (Exception e) {
            setLoading(false);
            Log.e(TAG, "Error during login: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка входа. Попробуйте снова.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInput(String email, String password) {
        if (ValidationUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            etEmail.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Введите корректный email");
            etEmail.requestFocus();
            return false;
        }
        if (ValidationUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Пароль должен быть не менее 6 символов");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
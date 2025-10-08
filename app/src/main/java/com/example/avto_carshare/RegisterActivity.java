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
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.User;
import com.example.avto_carshare.utils.SessionManager;
import com.example.avto_carshare.utils.ValidationUtils;

// Регистрация активности
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword, etDriverLicense;
    private Button btnRegister;
    private TextView tvLogin;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_register);
            dbHelper = new DatabaseHelper(this);
            sessionManager = new SessionManager(this);
            initViews();
            setupListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDriverLicense = findViewById(R.id.etDriverLicense);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish());

        etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            registerUser();
            return true;
        });
        setupFieldValidation();
    }

    // Валидация имени
    private void setupFieldValidation() {
        etFullName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String fullName = s.toString().trim();
                if (ValidationUtils.isEmpty(fullName)) {
                    etFullName.setError("Введите полное имя");
                } else if (!ValidationUtils.isValidName(fullName)) {
                    etFullName.setError("Имя должно содержать минимум 2 символа");
                } else {
                    etFullName.setError(null);
                }
            }
        });

        // Валидация email
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String email = s.toString().trim();
                if (ValidationUtils.isEmpty(email)) {
                    etEmail.setError("Введите email");
                } else if (!ValidationUtils.isValidEmail(email)) {
                    etEmail.setError("Введите корректный email");
                } else {
                    etEmail.setError(null);
                }
            }
        });

        // Валидация телефона
        etPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String phone = s.toString().trim();
                if (ValidationUtils.isEmpty(phone)) {
                    etPhone.setError("Введите номер телефона");
                } else if (!ValidationUtils.isValidPhone(phone)) {
                    etPhone.setError("Введите корректный номер телефона (например, +79991234567)");
                } else {
                    etPhone.setError(null);
                }
            }
        });

        // Валидация водительского удостоверения
        etDriverLicense.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String license = s.toString().trim();
                if (ValidationUtils.isEmpty(license)) {
                    etDriverLicense.setError("Введите номер водительского удостоверения");
                } else if (!ValidationUtils.isValidDriverLicense(license)) {
                    etDriverLicense.setError("Водительское удостоверение должно содержать до 10 цифр");
                } else {
                    etDriverLicense.setError(null);
                }
            }
        });

        // Валидация пароля
        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String password = s.toString().trim();
                if (ValidationUtils.isEmpty(password)) {
                    etPassword.setError("Введите пароль");
                } else if (!ValidationUtils.isValidPassword(password)) {
                    etPassword.setError("Пароль должен содержать минимум 6 символов");
                } else {
                    etPassword.setError(null);
                }
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                if (!ValidationUtils.isEmpty(confirmPassword) && !password.equals(confirmPassword)) {
                    etConfirmPassword.setError("Пароли не совпадают");
                } else if (!ValidationUtils.isEmpty(confirmPassword)) {
                    etConfirmPassword.setError(null);
                }
            }
        });

        // Валидация подтверждения пароля
        etConfirmPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String confirmPassword = s.toString().trim();
                String password = etPassword.getText().toString().trim();
                if (ValidationUtils.isEmpty(confirmPassword)) {
                    etConfirmPassword.setError("Подтвердите пароль");
                } else if (!password.equals(confirmPassword)) {
                    etConfirmPassword.setError("Пароли не совпадают");
                } else {
                    etConfirmPassword.setError(null);
                }
            }
        });
    }

    // Получение данных и валидация при регистрации
    private void registerUser() {
        try {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String driverLicense = etDriverLicense.getText().toString().trim();

            if (!validateInput(fullName, email, phone, password, confirmPassword)) {
                return;
            }

            setLoading(true);

            if (dbHelper.isEmailExists(email)) {
                setLoading(false);
                etEmail.setError("Пользователь с таким email уже существует");
                etEmail.requestFocus();
                Toast.makeText(this, "Email уже зарегистрирован", Toast.LENGTH_SHORT).show();
                return;
            }

            phone = ValidationUtils.normalizePhone(phone);

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email.toLowerCase());
            user.setPhone(phone);
            user.setPassword(password);
            user.setDriverLicense(driverLicense);
            user.setProfileImageUrl("");

            long userId = dbHelper.registerUser(user);

            setLoading(false);

            if (userId > 0) {
                Toast.makeText(this, "Регистрация успешна! Добро пожаловать!", Toast.LENGTH_SHORT).show();
                sessionManager.createLoginSession((int) userId, email, fullName);

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ошибка регистрации. Попробуйте снова.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            setLoading(false);
            Log.e(TAG, "Error during registration: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Валидация данных ввода
    private boolean validateInput(String fullName, String email, String phone,
                                  String password, String confirmPassword) {
        if (ValidationUtils.isEmpty(fullName)) {
            etFullName.setError("Введите полное имя");
            etFullName.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidName(fullName)) {
            etFullName.setError("Имя должно содержать минимум 2 символа");
            etFullName.requestFocus();
            return false;
        }
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
        if (ValidationUtils.isEmpty(phone)) {
            etPhone.setError("Введите номер телефона");
            etPhone.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            etPhone.setError("Введите корректный номер телефона (например, +79991234567)");
            etPhone.requestFocus();
            return false;
        }
        String driverLicense = etDriverLicense.getText().toString().trim();
        if (ValidationUtils.isEmpty(driverLicense)) {
            etDriverLicense.setError("Введите номер водительского удостоверения");
            etDriverLicense.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidDriverLicense(driverLicense)) {
            etDriverLicense.setError("Водительское удостоверение должно содержать до 10 цифр");
            etDriverLicense.requestFocus();
            return false;
        }
        if (ValidationUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Пароль должен содержать минимум 6 символов");
            etPassword.requestFocus();
            return false;
        }
        if (ValidationUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Подтвердите пароль");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Пароли не совпадают");
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnRegister.setEnabled(!isLoading);
        etFullName.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPhone.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
        etConfirmPassword.setEnabled(!isLoading);
        etDriverLicense.setEnabled(!isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
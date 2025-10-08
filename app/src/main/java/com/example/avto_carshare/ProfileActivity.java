package com.example.avto_carshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.model.User;
import com.example.avto_carshare.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Активность для управления профилем пользователя
public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvDriverLicense, tvRegistrationDate;
    private TextView tvTotalRentals, tvActiveRentals, tvTotalSpent;
    private Button btnEditProfile, btnChangePassword, btnSwitchAccount, btnLogout;
    private ImageView ivProfileAvatar;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView currentDialogImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageResult(imageUri);
                        }
                    }
                });

        initViews();
        loadUserData();
        setupListeners();
    }

    // Инициализация view-элементов
    private void initViews() {
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvDriverLicense = findViewById(R.id.tvDriverLicense);
        tvRegistrationDate = findViewById(R.id.tvRegistrationDate);
        tvTotalRentals = findViewById(R.id.tvTotalRentals);
        tvActiveRentals = findViewById(R.id.tvActiveRentals);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSwitchAccount = findViewById(R.id.btnSwitchAccount);
        btnLogout = findViewById(R.id.btnLogout);
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        ivProfileAvatar.setClipToOutline(true);
        ivProfileAvatar.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    }

    // Загрузка данных пользователя
    private void loadUserData() {
        int userId = sessionManager.getUserId();
        android.util.Log.d("ProfileActivity", "Loading user data for userId: " + userId);

        currentUser = dbHelper.getUserById(userId);

        if (currentUser == null) {
            android.util.Log.e("ProfileActivity", "User not found in database for userId: " + userId);
            Toast.makeText(this, "Ошибка загрузки данных пользователя. Попробуйте войти заново.", Toast.LENGTH_LONG).show();
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        android.util.Log.d("ProfileActivity", "User loaded successfully: " + currentUser.getEmail());

        tvFullName.setText(currentUser.getFullName());
        tvEmail.setText(currentUser.getEmail());
        tvPhone.setText(currentUser.getPhone() != null && !currentUser.getPhone().isEmpty()
                ? currentUser.getPhone() : "Не указан");
        tvDriverLicense.setText(currentUser.getDriverLicense() != null && !currentUser.getDriverLicense().isEmpty()
                ? currentUser.getDriverLicense() : "Не указано");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        tvRegistrationDate.setText(sdf.format(new Date(currentUser.getRegistrationDate())));

        loadRentalStatistics(userId);

        loadProfileImage();
    }

    // Загрузка статистики аренд пользователя
    private void loadRentalStatistics(int userId) {
        List<Rental> allRentals = dbHelper.getUserRentals(userId);
        List<Rental> activeRentals = dbHelper.getUserActiveRentals(userId);

        tvTotalRentals.setText(String.valueOf(allRentals.size()));
        tvActiveRentals.setText(String.valueOf(activeRentals.size()));

        double totalSpent = 0;
        for (Rental rental : allRentals) {
            if ("completed".equals(rental.getStatus())) {
                totalSpent += rental.getTotalPrice();
            }
        }
        tvTotalSpent.setText(String.format(Locale.getDefault(), "%.2f ₽", totalSpent));
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnSwitchAccount.setOnClickListener(v -> switchAccount());
        btnLogout.setOnClickListener(v -> logout());
    }

    // Отображение диалога редактирования профиля
    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "Ошибка: данные пользователя не загружены", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        com.google.android.material.textfield.TextInputEditText etFullName = dialogView.findViewById(R.id.etFullName);
        com.google.android.material.textfield.TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        com.google.android.material.textfield.TextInputEditText etDriverLicense = dialogView.findViewById(R.id.etDriverLicense);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        ImageView ivProfileImage = dialogView.findViewById(R.id.ivProfileImage);
        com.google.android.material.textfield.TextInputLayout tilFullName =
                (com.google.android.material.textfield.TextInputLayout) etFullName.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilPhone =
                (com.google.android.material.textfield.TextInputLayout) etPhone.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilDriverLicense =
                (com.google.android.material.textfield.TextInputLayout) etDriverLicense.getParent().getParent();

        if (etFullName == null || etPhone == null || etDriverLicense == null || btnSave == null || btnCancel == null || ivProfileImage == null) {
            Toast.makeText(this, "Ошибка загрузки формы редактирования", Toast.LENGTH_SHORT).show();
            return;
        }

        etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        etDriverLicense.setText(currentUser.getDriverLicense() != null ? currentUser.getDriverLicense() : "");

        loadProfileImage(ivProfileImage);

        ivProfileImage.setClipToOutline(true);
        ivProfileImage.setOutlineProvider(ViewOutlineProvider.BACKGROUND);

        currentDialogImageView = ivProfileImage;

        setupProfileFieldValidation(etFullName, tilFullName, etPhone, tilPhone, etDriverLicense, tilDriverLicense);

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String driverLicense = etDriverLicense.getText() != null ? etDriverLicense.getText().toString().trim() : "";
            if (fullName.isEmpty()) {
                tilFullName.setError("Имя не может быть пустым");
                return;
            } else if (fullName.length() < 2) {
                tilFullName.setError("Имя должно содержать минимум 2 символа");
                return;
            } else {
                tilFullName.setError(null);
            }
            if (phone.isEmpty()) {
                tilPhone.setError("Введите номер телефона");
                return;
            } else if (!com.example.avto_carshare.utils.ValidationUtils.isValidPhone(phone)) {
                tilPhone.setError("Введите корректный номер телефона (например, +79991234567)");
                return;
            } else {
                tilPhone.setError(null);
            }
            if (driverLicense.isEmpty()) {
                tilDriverLicense.setError("Введите номер водительского удостоверения");
                return;
            } else if (!com.example.avto_carshare.utils.ValidationUtils.isValidDriverLicense(driverLicense)) {
                tilDriverLicense.setError("Водительское удостоверение должно содержать до 10 цифр");
                return;
            } else {
                tilDriverLicense.setError(null);
            }

            phone = com.example.avto_carshare.utils.ValidationUtils.normalizePhone(phone);

            currentUser.setFullName(fullName);
            currentUser.setPhone(phone);
            currentUser.setDriverLicense(driverLicense);

            try {
                int result = dbHelper.updateUser(currentUser);
                if (result > 0) {
                    Toast.makeText(this, "Профиль обновлен", Toast.LENGTH_SHORT).show();
                    sessionManager.createLoginSession(currentUser.getId(), currentUser.getEmail(), fullName);
                    loadUserData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        ivProfileImage.setOnClickListener(v -> openGallery());

        try {
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка открытия диалога: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Загрузка изображения профиля
    private void loadProfileImage() {
        String imagePath = currentUser.getProfileImageUrl();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ivProfileAvatar.setImageURI(Uri.fromFile(imageFile));
            } else {
                ivProfileAvatar.setImageResource(R.drawable.ic_default_profile);
            }
        } else {
            ivProfileAvatar.setImageResource(R.drawable.ic_default_profile);
        }
    }

    private void loadProfileImage(ImageView imageView) {
        String imagePath = currentUser.getProfileImageUrl();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imageFile));
            } else {
                imageView.setImageResource(R.drawable.ic_default_profile);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_default_profile);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleImageResult(imageUri);
                    }
                }
            });

    // Загрузка пользовательского изображения
    private void handleImageResult(Uri imageUri) {
        try {
            String oldImagePath = currentUser.getProfileImageUrl();
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                File oldImageFile = new File(oldImagePath);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            File filesDir = getFilesDir();
            File profileImagesDir = new File(filesDir, "profile_images");
            if (!profileImagesDir.exists()) {
                profileImagesDir.mkdirs();
            }

            String fileName = "profile_" + currentUser.getId() + "_" + System.currentTimeMillis() + ".jpg";
            File newImageFile = new File(profileImagesDir, fileName);

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(newImageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            currentUser.setProfileImageUrl(newImageFile.getAbsolutePath());

            dbHelper.updateUser(currentUser);

            if (currentDialogImageView != null) {
                currentDialogImageView.setImageURI(Uri.fromFile(newImageFile));
            }

            loadProfileImage();

            Toast.makeText(this, "Изображение профиля обновлено", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки изображения: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("ProfileActivity", "Error handling image", e);
        }
    }

    // Валидация полей профиля
    private void setupProfileFieldValidation(
            com.google.android.material.textfield.TextInputEditText etFullName,
            com.google.android.material.textfield.TextInputLayout tilFullName,
            com.google.android.material.textfield.TextInputEditText etPhone,
            com.google.android.material.textfield.TextInputLayout tilPhone,
            com.google.android.material.textfield.TextInputEditText etDriverLicense,
            com.google.android.material.textfield.TextInputLayout tilDriverLicense) {

        etFullName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String fullName = s.toString().trim();
                if (fullName.isEmpty()) {
                    tilFullName.setError("Имя не может быть пустым");
                } else if (fullName.length() < 2) {
                    tilFullName.setError("Имя должно содержать минимум 2 символа");
                } else {
                    tilFullName.setError(null);
                }
            }
        });

        etPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String phone = s.toString().trim();
                if (!phone.isEmpty() && !com.example.avto_carshare.utils.ValidationUtils.isValidPhone(phone)) {
                    tilPhone.setError("Введите корректный номер телефона (например, +79991234567)");
                } else {
                    tilPhone.setError(null);
                }
            }
        });

        etDriverLicense.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String license = s.toString().trim();
                if (!license.isEmpty() && !com.example.avto_carshare.utils.ValidationUtils.isValidDriverLicense(license)) {
                    tilDriverLicense.setError("Водительское удостоверение должно содержать до 10 цифр");
                } else {
                    tilDriverLicense.setError(null);
                }
            }
        });
    }

    // Диалог смены пароля
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);
        Button btnChange = dialogView.findViewById(R.id.btnChange);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnChange.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Новый пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Новые пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.changePassword(currentUser.getId(), oldPassword, newPassword);
            if (success) {
                Toast.makeText(this, "Пароль изменен", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Неверный старый пароль", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Диалог смены аккаунта
    private void switchAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Смена аккаунта");
        builder.setMessage("Вы уверены, что хотите сменить аккаунт?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // Диалог выхода
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выход");
        builder.setMessage("Вы уверены, что хотите выйти?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}
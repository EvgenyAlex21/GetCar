package com.example.avto_carshare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.avto_carshare.adapter.CarAdapter;
import com.example.avto_carshare.adapter.RentalAdapter;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.service.NotificationService;
import com.example.avto_carshare.service.RentalStatusService;
import com.example.avto_carshare.utils.SessionManager;
import com.example.avto_carshare.utils.ValidationUtils;
import com.example.avto_carshare.utils.CarTypeUtils;
import com.example.avto_carshare.utils.ColorUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Главная активность приложения с вкладками для автомобилей и аренд
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private Spinner spinnerCarType;
    private Button btnFilter, btnShowAll;
    private ExtendedFloatingActionButton fabAddCar;
    private MaterialCardView filterLayout;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private int currentTabPosition = 0;
    private RentalStatusService rentalStatusService;
    private NotificationService notificationService;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        dbHelper = new DatabaseHelper(this);

        notificationService = new NotificationService(this);
        rentalStatusService = new RentalStatusService(this);

        setupRecyclerView();
        setupTabs();
        setupFilter();
        loadCars();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rentalStatusService != null) {
            rentalStatusService.startMonitoring();
        }

        if (currentTabPosition == 0) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadCars();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_rentals);
            loadRentals();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rentalStatusService != null) {
            rentalStatusService.stopMonitoring();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rentalStatusService != null) {
            rentalStatusService.stopMonitoring();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        spinnerCarType = findViewById(R.id.spinnerCarType);
        btnFilter = findViewById(R.id.btnFilter);
        btnShowAll = findViewById(R.id.btnShowAll);
        fabAddCar = findViewById(R.id.fabAddCar);
        filterLayout = findViewById(R.id.filterLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("GetCar");
        }
    }

    // Настройка нижней навигации
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                if (tabLayout.getSelectedTabPosition() != 0) {
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                }
                return true;
            } else if (itemId == R.id.nav_rentals) {
                if (tabLayout.getSelectedTabPosition() != 1) {
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    if (tab != null) {
                        tab.select();
                    }
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            }

            return false;
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Обработка выбора пунктов меню
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_statistics) {
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        } else if (id == R.id.action_view_all_cars) {
            viewAllCars();
            return true;
        } else if (id == R.id.action_switch_account) {
            switchAccount();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Просмотр всех автомобилей
    private void viewAllCars() {
        if (tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }
        spinnerCarType.setSelection(0);
        List<Car> allCars = dbHelper.getAllCars();
        if (carAdapter != null) {
            carAdapter.updateData(allCars);
        }
    }

    // Настройка вкладок
    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                if (tab.getPosition() == 0) {
                    filterLayout.setVisibility(View.VISIBLE);
                    fabAddCar.show();
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                    loadCars();
                } else {
                    filterLayout.setVisibility(View.GONE);
                    fabAddCar.hide();
                    bottomNavigationView.setSelectedItemId(R.id.nav_rentals);
                    loadRentals();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // Настройка фильтра
    private void setupFilter() {
        String[] carTypes = {
                getString(R.string.filter_all_types),
                getString(R.string.filter_economy),
                getString(R.string.filter_comfort),
                getString(R.string.filter_premium)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, carTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarType.setAdapter(adapter);

        btnFilter.setOnClickListener(v -> {
            String selectedType = spinnerCarType.getSelectedItem().toString();
            applyFilter(selectedType);
        });

        btnShowAll.setOnClickListener(v -> {
            spinnerCarType.setSelection(0);
            loadCars();
        });

        fabAddCar.setOnClickListener(v -> showAddCarDialog());
    }

    // Применение фильтра по типу автомобиля
    private void applyFilter(String selectedType) {
        List<Car> filteredCars;
        if (!selectedType.equals(getString(R.string.filter_all_types))) {
            filteredCars = dbHelper.searchCarsByType(selectedType);
            List<Car> availableCars = new ArrayList<>();
            for (Car car : filteredCars) {
                if (car.isAvailable()) {
                    availableCars.add(car);
                }
            }
            filteredCars = availableCars;
        } else {
            filteredCars = dbHelper.getAvailableCars();
        }

        if (carAdapter != null) {
            carAdapter.updateData(filteredCars);
        }

        if (filteredCars.isEmpty()) {
            Toast.makeText(this, "Автомобилей не найдено", Toast.LENGTH_SHORT).show();
        }
    }

    // Загрузка списка автомобилей
    private void loadCars() {
        List<Car> cars = dbHelper.getAllCars();

        if (tvEmptyMessage != null) {
            if (cars.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Автомобили не найдены в базе данных");
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        carAdapter = new CarAdapter(this, cars, new CarAdapter.OnCarClickListener() {
            @Override
            public void onCarClick(Car car) {
                showCarDetailsDialog(car);
            }

            @Override
            public void onRentClick(Car car) {
                if (car.isAvailable()) {
                    showRentCarDialog(car);
                } else {
                    Toast.makeText(MainActivity.this, "Автомобиль уже арендован", Toast.LENGTH_SHORT).show();
                    loadCars();
                }
            }
        });
        recyclerView.setAdapter(carAdapter);
    }

    // Загрузка списка аренд пользователя
    private void loadRentals() {
        int userId = sessionManager.getUserId();
        List<Rental> rentals = dbHelper.getUserRentals(userId);

        if (tvEmptyMessage != null) {
            if (rentals.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("У вас пока нет аренд.\nАрендуйте автомобиль на вкладке 'Автомобили'.");
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        RentalAdapter rentalAdapter = new RentalAdapter(this, rentals, new RentalAdapter.OnRentalActionListener() {
            @Override
            public void onCompleteRental(Rental rental) {
                completeRental(rental);
            }

            @Override
            public void onCancelRental(Rental rental) {
                cancelRental(rental);
            }
        });
        recyclerView.setAdapter(rentalAdapter);
    }

    // Отображение детальной информации об автомобиле
    private void showCarDetailsDialog(Car car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(car.getBrand() + " " + car.getModel());

        String colorName = getColorName(car.getColor());
        String details = getString(R.string.detail_year, car.getYear()) + "\n" +
                getString(R.string.detail_type, car.getCarType()) + "\n" +
                "Цвет: " + colorName + "\n" +
                getString(R.string.detail_license, car.getLicensePlate()) + "\n" +
                getString(R.string.detail_location, car.getLocation()) + "\n" +
                getString(R.string.detail_seats, car.getSeats()) + "\n" +
                getString(R.string.detail_fuel, car.getFuelLevel()) + "\n" +
                getString(R.string.detail_price, car.getPricePerHour(), car.getPricePerDay()) + "\n" +
                (car.isAvailable() ? getString(R.string.detail_status_available) : getString(R.string.detail_status_unavailable));

        builder.setMessage(details);
        builder.setPositiveButton(R.string.btn_ok, null);
        if (car.isAvailable()) {
            builder.setNeutralButton(R.string.btn_rent, (dialog, which) -> showRentCarDialog(car));
        }
        builder.show();
    }

    // Получение названия цвета по HEX-коду
    private String getColorName(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            return "Неизвестный";
        }

        switch (hexColor.toUpperCase()) {
            case "#FFFFFF": return "Белый";
            case "#000000": return "Черный";
            case "#FF0000": return "Красный";
            case "#0000FF": return "Синий";
            case "#808080": return "Серый";
            case "#C0C0C0": return "Серебристый";
            case "#FFA500": return "Оранжевый";
            case "#008000": return "Зеленый";
            case "#ADD8E6": return "Голубой";
            default: return "Цветной";
        }
    }

    // Диалог аренды автомобиля
    private void showRentCarDialog(Car car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rent_car, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView tvCarName = dialogView.findViewById(R.id.tvCarName);
        Button btnPickStartDate = dialogView.findViewById(R.id.btnPickStartDate);
        Button btnPickStartTime = dialogView.findViewById(R.id.btnPickStartTime);
        Button btnPickEndDate = dialogView.findViewById(R.id.btnPickEndDate);
        Button btnPickEndTime = dialogView.findViewById(R.id.btnPickEndTime);
        TextView tvStartDateTime = dialogView.findViewById(R.id.tvStartDateTime);
        TextView tvEndDateTime = dialogView.findViewById(R.id.tvEndDateTime);
        TextView tvCalculatedPrice = dialogView.findViewById(R.id.tvCalculatedPrice);
        TextView tvDuration = dialogView.findViewById(R.id.tvDuration);
        Button btnConfirmRental = dialogView.findViewById(R.id.btnConfirmRental);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);

        tvCarName.setText(String.format(Locale.getDefault(), "%s %s (%s)",
                car.getBrand(), car.getModel(), car.getLicensePlate()));

        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR_OF_DAY, 4);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        btnPickStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvStartDateTime.setText(sdf.format(startCalendar.getTime()));
                        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);
                    },
                    startCalendar.get(Calendar.YEAR),
                    startCalendar.get(Calendar.MONTH),
                    startCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        btnPickStartTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCalendar.set(Calendar.MINUTE, minute);
                        tvStartDateTime.setText(sdf.format(startCalendar.getTime()));
                        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);
                    },
                    startCalendar.get(Calendar.HOUR_OF_DAY),
                    startCalendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        btnPickEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, month);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvEndDateTime.setText(sdf.format(endCalendar.getTime()));
                        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);
                    },
                    endCalendar.get(Calendar.YEAR),
                    endCalendar.get(Calendar.MONTH),
                    endCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        btnPickEndTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCalendar.set(Calendar.MINUTE, minute);
                        tvEndDateTime.setText(sdf.format(endCalendar.getTime()));
                        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);
                    },
                    endCalendar.get(Calendar.HOUR_OF_DAY),
                    endCalendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        tvStartDateTime.setText(sdf.format(startCalendar.getTime()));
        tvEndDateTime.setText(sdf.format(endCalendar.getTime()));
        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);

        // Подтверждение аренды
        btnConfirmRental.setOnClickListener(v -> {
            if (endCalendar.getTimeInMillis() <= startCalendar.getTimeInMillis()) {
                Toast.makeText(this, R.string.msg_end_time_error, Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = calculateTotalPrice(car, startCalendar, endCalendar);

            Rental rental = new Rental();
            rental.setCarId(car.getId());
            rental.setUserId(sessionManager.getUserId());
            rental.setStartTime(startCalendar.getTimeInMillis());
            rental.setEndTime(endCalendar.getTimeInMillis());
            rental.setTotalPrice(totalPrice);
            rental.setStatus("active");
            rental.setPickupLocation(car.getLocation());
            rental.setReturnLocation(car.getLocation());

            long rentalId = dbHelper.addRental(rental);
            dbHelper.updateCarAvailability(car.getId(), false);

            if (rentalId > 0) {
                rental.setId((int) rentalId);

                rentalStatusService.scheduleRentalExpiration(rental);

                notificationService.sendRentalStartedNotification(rental, car);

                Toast.makeText(this, R.string.msg_rental_success, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                loadCars();
            } else {
                Toast.makeText(this, R.string.msg_rental_error, Toast.LENGTH_SHORT).show();
            }
        });
        btnCancelDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Расчет стоимости аренды
    private void calculatePrice(Car car, Calendar start, Calendar end, TextView tvPrice, TextView tvDuration) {
        double price = calculateTotalPrice(car, start, end);
        long durationMillis = end.getTimeInMillis() - start.getTimeInMillis();

        long minutes = durationMillis / (1000 * 60);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        long days = hours / 24;
        long remainingHours = hours % 24;

        tvPrice.setText(getString(R.string.calculated_price, price));

        if (days > 0) {
            tvDuration.setText(String.format(Locale.getDefault(), "%d дн. %d ч. %d мин.", days, remainingHours, remainingMinutes));
        } else if (hours > 0) {
            tvDuration.setText(String.format(Locale.getDefault(), "%d ч. %d мин.", hours, remainingMinutes));
        } else {
            tvDuration.setText(String.format(Locale.getDefault(), "%d мин.", minutes));
        }
    }

    // Вычисление общей стоимости аренды
    private double calculateTotalPrice(Car car, Calendar start, Calendar end) {
        long durationMillis = end.getTimeInMillis() - start.getTimeInMillis();

        long totalMinutes = durationMillis / (1000 * 60);

        if (totalMinutes <= 0) {
            return 0;
        }

        double minuteRate = car.getPricePerHour() / 60.0;

        long days = totalMinutes / (24 * 60);
        long remainingMinutes = totalMinutes % (24 * 60);

        double price = 0;

        if (days > 0) {
            price += days * car.getPricePerDay();
        }

        price += remainingMinutes * minuteRate;

        return price;
    }

    // Диалог добавления нового автомобиля
    private void showAddCarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_car_new, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText etBrand = dialogView.findViewById(R.id.etBrand);
        EditText etModel = dialogView.findViewById(R.id.etModel);
        EditText etLicensePlate = dialogView.findViewById(R.id.etLicensePlate);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etPricePerHour = dialogView.findViewById(R.id.etPricePerHour);
        EditText etPricePerDay = dialogView.findViewById(R.id.etPricePerDay);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etFuelLevel = dialogView.findViewById(R.id.etFuelLevel);
        EditText etSeats = dialogView.findViewById(R.id.etSeats);
        Button btnAddCar = dialogView.findViewById(R.id.btnAddCar);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        com.google.android.material.textfield.TextInputLayout tilBrand =
                (com.google.android.material.textfield.TextInputLayout) etBrand.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilModel =
                (com.google.android.material.textfield.TextInputLayout) etModel.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilLicensePlate =
                (com.google.android.material.textfield.TextInputLayout) etLicensePlate.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilYear =
                (com.google.android.material.textfield.TextInputLayout) etYear.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilPricePerHour =
                (com.google.android.material.textfield.TextInputLayout) etPricePerHour.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilPricePerDay =
                (com.google.android.material.textfield.TextInputLayout) etPricePerDay.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilLocation =
                (com.google.android.material.textfield.TextInputLayout) etLocation.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilFuelLevel =
                (com.google.android.material.textfield.TextInputLayout) etFuelLevel.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout tilSeats =
                (com.google.android.material.textfield.TextInputLayout) etSeats.getParent().getParent();

        android.widget.AutoCompleteTextView spinnerCarType = dialogView.findViewById(R.id.spinnerCarType);
        com.google.android.material.textfield.TextInputLayout tilCarType =
                (com.google.android.material.textfield.TextInputLayout) spinnerCarType.getParent().getParent();
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CarTypeUtils.getCarTypes());
        spinnerCarType.setAdapter(typeAdapter);
        spinnerCarType.setOnClickListener(v -> spinnerCarType.showDropDown());

        android.widget.AutoCompleteTextView spinnerColor = dialogView.findViewById(R.id.spinnerColor);
        com.google.android.material.textfield.TextInputLayout tilColor =
                (com.google.android.material.textfield.TextInputLayout) spinnerColor.getParent().getParent();
        List<ColorUtils.ColorItem> colors = ColorUtils.getAvailableColors();
        ArrayAdapter<ColorUtils.ColorItem> colorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, colors);
        spinnerColor.setAdapter(colorAdapter);
        spinnerColor.setOnClickListener(v -> spinnerColor.showDropDown());

        etBrand.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilBrand.setError("Введите марку автомобиля");
                } else {
                    tilBrand.setError(null);
                    tilBrand.setErrorEnabled(false);
                }
            }
        });

        etModel.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilModel.setError("Введите модель автомобиля");
                } else {
                    tilModel.setError(null);
                    tilModel.setErrorEnabled(false);
                }
            }
        });

        etLicensePlate.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String licensePlate = s.toString().trim().toUpperCase();
                if (!ValidationUtils.isValidLicensePlate(licensePlate)) {
                    tilLicensePlate.setError("Неверный формат госномера (например: А123АА777)");
                } else {
                    tilLicensePlate.setError(null);
                    tilLicensePlate.setErrorEnabled(false);
                }
            }
        });

        etYear.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilYear.setError("Введите год выпуска");
                } else if (!ValidationUtils.isValidYear(s.toString())) {
                    tilYear.setError("Год должен быть от 1886 до " + (Calendar.getInstance().get(Calendar.YEAR) + 1));
                } else {
                    tilYear.setError(null);
                    tilYear.setErrorEnabled(false);
                }
            }
        });

        etPricePerHour.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    if (ValidationUtils.isEmpty(s.toString())) {
                        tilPricePerHour.setError("Введите цену за час");
                    } else if (Double.parseDouble(s.toString()) <= 0) {
                        tilPricePerHour.setError("Цена должна быть больше 0");
                    } else {
                        tilPricePerHour.setError(null);
                        tilPricePerHour.setErrorEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    tilPricePerHour.setError("Введите корректное число");
                }
            }
        });

        etPricePerDay.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    if (ValidationUtils.isEmpty(s.toString())) {
                        tilPricePerDay.setError("Введите цену за день");
                    } else if (Double.parseDouble(s.toString()) <= 0) {
                        tilPricePerDay.setError("Цена должна быть больше 0");
                    } else {
                        tilPricePerDay.setError(null);
                        tilPricePerDay.setErrorEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    tilPricePerDay.setError("Введите корректное число");
                }
            }
        });

        etLocation.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilLocation.setError("Введите местоположение автомобиля");
                } else {
                    tilLocation.setError(null);
                    tilLocation.setErrorEnabled(false);
                }
            }
        });

        etFuelLevel.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    if (ValidationUtils.isEmpty(s.toString())) {
                        tilFuelLevel.setError("Введите уровень топлива");
                    } else {
                        double fuelLevel = Double.parseDouble(s.toString());
                        if (fuelLevel < 0 || fuelLevel > 100) {
                            tilFuelLevel.setError("Уровень топлива должен быть от 0 до 100%");
                        } else {
                            tilFuelLevel.setError(null);
                            tilFuelLevel.setErrorEnabled(false);
                        }
                    }
                } catch (NumberFormatException e) {
                    tilFuelLevel.setError("Введите корректное число");
                }
            }
        });

        etSeats.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    if (ValidationUtils.isEmpty(s.toString())) {
                        tilSeats.setError("Введите количество мест");
                    } else if (!ValidationUtils.isValidSeats(s.toString())) {
                        tilSeats.setError("Количество мест должно быть от 1 до 50");
                    } else {
                        tilSeats.setError(null);
                        tilSeats.setErrorEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    tilSeats.setError("Введите корректное число");
                }
            }
        });

        spinnerCarType.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilCarType.setError("Выберите тип автомобиля");
                } else {
                    tilCarType.setError(null);
                    tilCarType.setErrorEnabled(false);
                }
            }
        });

        spinnerColor.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (ValidationUtils.isEmpty(s.toString())) {
                    tilColor.setError("Выберите цвет автомобиля");
                } else {
                    tilColor.setError(null);
                    tilColor.setErrorEnabled(false);
                }
            }
        });

        android.text.TextWatcher enableButtonWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                validateAddCarButton(btnAddCar, etBrand, etModel, etLicensePlate, etYear,
                        etPricePerHour, etPricePerDay, etLocation, etFuelLevel, etSeats,
                        spinnerCarType, spinnerColor);
            }
        };

        etBrand.addTextChangedListener(enableButtonWatcher);
        etModel.addTextChangedListener(enableButtonWatcher);
        etLicensePlate.addTextChangedListener(enableButtonWatcher);
        etYear.addTextChangedListener(enableButtonWatcher);
        etPricePerHour.addTextChangedListener(enableButtonWatcher);
        etPricePerDay.addTextChangedListener(enableButtonWatcher);
        etLocation.addTextChangedListener(enableButtonWatcher);
        etFuelLevel.addTextChangedListener(enableButtonWatcher);
        etSeats.addTextChangedListener(enableButtonWatcher);
        spinnerCarType.addTextChangedListener(enableButtonWatcher);
        spinnerColor.addTextChangedListener(enableButtonWatcher);

        btnAddCar.setOnClickListener(v -> {
            try {
                String brand = etBrand.getText().toString().trim();
                String model = etModel.getText().toString().trim();
                String licensePlate = etLicensePlate.getText().toString().trim().toUpperCase();
                String yearStr = etYear.getText().toString().trim();
                String pricePerHourStr = etPricePerHour.getText().toString().trim();
                String pricePerDayStr = etPricePerDay.getText().toString().trim();
                String location = etLocation.getText().toString().trim();
                String fuelLevelStr = etFuelLevel.getText().toString().trim();
                String seatsStr = etSeats.getText().toString().trim();
                String selectedType = spinnerCarType.getText().toString().trim();
                String selectedColorName = spinnerColor.getText().toString().trim();
                String colorHex = ColorUtils.getColorHex(selectedColorName);
                Car car = new Car();
                car.setBrand(brand);
                car.setModel(model);
                car.setLicensePlate(licensePlate);
                car.setCarType(selectedType);
                car.setYear(Integer.parseInt(yearStr));
                car.setColor(colorHex);
                car.setPricePerHour(Double.parseDouble(pricePerHourStr));
                car.setPricePerDay(Double.parseDouble(pricePerDayStr));
                car.setLocation(location);
                car.setFuelLevel(Double.parseDouble(fuelLevelStr));
                car.setSeats(Integer.parseInt(seatsStr));
                car.setAvailable(true);
                car.setImageUrl("");
                String validationError = ValidationUtils.validateCarData(car);
                if (validationError != null) {
                    Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
                    return;
                }

                long carId = dbHelper.addCar(car);
                if (carId > 0) {
                    Toast.makeText(this, "✅ Автомобиль успешно добавлен!", Toast.LENGTH_SHORT).show();

                    notificationService.sendNewCarsAvailableNotification();

                    dialog.dismiss();
                    loadCars();
                } else {
                    Toast.makeText(this, "❌ Ошибка добавления автомобиля", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "⚠️ Проверьте числовые поля (год, цены, топливо, места)", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "❌ Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Валидация кнопки добавления автомобиля
    private void validateAddCarButton(Button btnAddCar, EditText etBrand, EditText etModel,
                                     EditText etLicensePlate, EditText etYear,
                                     EditText etPricePerHour, EditText etPricePerDay,
                                     EditText etLocation, EditText etFuelLevel,
                                     EditText etSeats, android.widget.AutoCompleteTextView spinnerCarType,
                                     android.widget.AutoCompleteTextView spinnerColor) {

        boolean isValid = true;
        if (ValidationUtils.isEmpty(etBrand.getText().toString().trim())) {
            isValid = false;
        }
        if (ValidationUtils.isEmpty(etModel.getText().toString().trim())) {
            isValid = false;
        }
        String licensePlate = etLicensePlate.getText().toString().trim().toUpperCase();
        if (!ValidationUtils.isValidLicensePlate(licensePlate)) {
            isValid = false;
        }
        String yearStr = etYear.getText().toString().trim();
        if (!ValidationUtils.isValidYear(yearStr)) {
            isValid = false;
        }
        try {
            String pricePerHourStr = etPricePerHour.getText().toString().trim();
            if (ValidationUtils.isEmpty(pricePerHourStr) || Double.parseDouble(pricePerHourStr) <= 0) {
                isValid = false;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        try {
            String pricePerDayStr = etPricePerDay.getText().toString().trim();
            if (ValidationUtils.isEmpty(pricePerDayStr) || Double.parseDouble(pricePerDayStr) <= 0) {
                isValid = false;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        if (ValidationUtils.isEmpty(etLocation.getText().toString().trim())) {
            isValid = false;
        }
        try {
            String fuelLevelStr = etFuelLevel.getText().toString().trim();
            if (ValidationUtils.isEmpty(fuelLevelStr)) {
                isValid = false;
            } else {
                double fuelLevel = Double.parseDouble(fuelLevelStr);
                if (fuelLevel < 0 || fuelLevel > 100) {
                    isValid = false;
                }
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        try {
            String seatsStr = etSeats.getText().toString().trim();
            if (!ValidationUtils.isValidSeats(seatsStr)) {
                isValid = false;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        if (ValidationUtils.isEmpty(spinnerCarType.getText().toString().trim())) {
            isValid = false;
        }
        if (ValidationUtils.isEmpty(spinnerColor.getText().toString().trim())) {
            isValid = false;
        }
        btnAddCar.setEnabled(isValid);
        btnAddCar.setAlpha(isValid ? 1.0f : 0.5f);
    }

    // Завершение аренды
    private void completeRental(Rental rental) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_complete_rental);
        builder.setMessage(R.string.dialog_complete_message);
        builder.setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
            dbHelper.updateRentalStatus(rental.getId(), "completed");
            dbHelper.updateCarAvailability(rental.getCarId(), true);
            dbHelper.decreaseCarFuelAfterRental(rental.getCarId());
            Toast.makeText(this, R.string.msg_rental_completed, Toast.LENGTH_SHORT).show();
            loadRentals();
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    // Отмена аренды
    private void cancelRental(Rental rental) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_cancel_rental);
        builder.setMessage(R.string.dialog_cancel_message);
        builder.setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
            dbHelper.updateRentalStatus(rental.getId(), "cancelled");
            dbHelper.updateCarAvailability(rental.getCarId(), true);
            Toast.makeText(this, R.string.msg_rental_cancelled, Toast.LENGTH_SHORT).show();
            loadRentals();
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    // Смена аккаунта
    private void switchAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сменить аккаунт");
        builder.setMessage("Вы уверены, что хотите сменить аккаунт? Текущая сессия будет завершена.");
        builder.setPositiveButton("Да", (dialog, which) -> {
            sessionManager.logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // Выход из приложения
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выход");
        builder.setMessage("Вы уверены, что хотите выйти из приложения?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            sessionManager.logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}
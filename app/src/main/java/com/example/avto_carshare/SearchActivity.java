package com.example.avto_carshare;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.avto_carshare.adapter.CarAdapter;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.utils.SessionManager;
import com.example.avto_carshare.service.NotificationService;
import com.example.avto_carshare.service.RentalStatusService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Активность для поиска и аренды автомобилей
public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private DatabaseHelper dbHelper;
    private List<Car> allCars;
    private SessionManager sessionManager;
    private NotificationService notificationService;
    private RentalStatusService rentalStatusService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        notificationService = new NotificationService(this);
        rentalStatusService = new RentalStatusService(this);

        initViews();
        loadAllCars();
        setupSearch();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadAllCars() {
        allCars = dbHelper.getAllCars();
        displayCars(allCars);
    }

    // Отображение списка автомобилей
    private void displayCars(List<Car> cars) {
        carAdapter = new CarAdapter(this, cars, new CarAdapter.OnCarClickListener() {
            @Override
            public void onCarClick(Car car) {
                showCarDetails(car);
            }

            @Override
            public void onRentClick(Car car) {
                if (car.isAvailable()) {
                    showRentCarDialog(car);
                } else {
                    Toast.makeText(SearchActivity.this,
                            "Автомобиль недоступен", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(carAdapter);
    }

    // Настройка поиска автомобилей
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCars(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Фильтрация автомобилей по запросу
    private void filterCars(String query) {
        List<Car> filteredCars = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.isEmpty()) {
            filteredCars = allCars;
        } else {
            for (Car car : allCars) {
                if (car.getBrand().toLowerCase().contains(lowerQuery) ||
                    car.getModel().toLowerCase().contains(lowerQuery) ||
                    car.getLicensePlate().toLowerCase().contains(lowerQuery) ||
                    car.getCarType().toLowerCase().contains(lowerQuery) ||
                    car.getColor().toLowerCase().contains(lowerQuery) ||
                    car.getLocation().toLowerCase().contains(lowerQuery)) {
                    filteredCars.add(car);
                }
            }
        }
        if (carAdapter != null) {
            carAdapter.updateData(filteredCars);
        }
    }

    // Отображение детальной информации об автомобиле в диалоге
    private void showCarDetails(Car car) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
            builder.setNeutralButton("Арендовать", (dialog, which) -> showRentCarDialog(car));
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

    // Отображение диалога для аренды автомобиля
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
        tvStartDateTime.setText(sdf.format(startCalendar.getTime()));
        tvEndDateTime.setText(sdf.format(endCalendar.getTime()));

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

        // Обработчики для выбора даты и времени начала и конец
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

        calculatePrice(car, startCalendar, endCalendar, tvCalculatedPrice, tvDuration);

        btnConfirmRental.setOnClickListener(v -> {
            long startTime = startCalendar.getTimeInMillis();
            long endTime = endCalendar.getTimeInMillis();

            if (endTime <= startTime) {
                Toast.makeText(this, "Время окончания должно быть позже времени начала", Toast.LENGTH_SHORT).show();
                return;
            }

            String priceText = tvCalculatedPrice.getText().toString();
            double totalPrice = Double.parseDouble(priceText.replaceAll("[^0-9.]", ""));

            Rental rental = new Rental();
            rental.setCarId(car.getId());
            rental.setUserId(sessionManager.getUserId());
            rental.setStartTime(startTime);
            rental.setEndTime(endTime);
            rental.setTotalPrice(totalPrice);
            rental.setStatus("active");
            rental.setPickupLocation(car.getLocation());
            rental.setReturnLocation(car.getLocation());

            long rentalId = dbHelper.addRental(rental);
            if (rentalId > 0) {
                rental.setId((int) rentalId);
                rentalStatusService.scheduleRentalExpiration(rental);
                notificationService.sendRentalStartedNotification(rental, car);
                dbHelper.updateCarAvailability(car.getId(), false);
                Toast.makeText(this, "Автомобиль успешно арендован!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadAllCars();
            } else {
                Toast.makeText(this, "Ошибка при создании аренды", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Расчет стоимости и длительности аренды
    private void calculatePrice(Car car, Calendar start, Calendar end,
                                 TextView tvPrice, TextView tvDuration) {
        long diffMillis = end.getTimeInMillis() - start.getTimeInMillis();
        long minutes = diffMillis / (1000 * 60);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        long days = hours / 24;
        long remainingHours = hours % 24;
        double totalPrice;

        if (days > 0) {
            totalPrice = (days * car.getPricePerDay()) +
                        (remainingHours * car.getPricePerHour()) +
                        (remainingMinutes * (car.getPricePerHour() / 60.0));
            tvDuration.setText(String.format(Locale.getDefault(), "%d дн. %d ч. %d мин.",
                    days, remainingHours, remainingMinutes));
        } else if (hours > 0) {
            totalPrice = (hours * car.getPricePerHour()) +
                        (remainingMinutes * (car.getPricePerHour() / 60.0));
            tvDuration.setText(String.format(Locale.getDefault(), "%d ч. %d мин.",
                    hours, remainingMinutes));
        } else {
            totalPrice = minutes * (car.getPricePerHour() / 60.0);
            tvDuration.setText(String.format(Locale.getDefault(), "%d мин.", minutes));
        }

        tvPrice.setText(String.format(Locale.getDefault(), "%.2f ₽", totalPrice));
    }
}
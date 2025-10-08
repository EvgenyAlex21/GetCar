package com.example.avto_carshare;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import java.util.Locale;

// Активность для отображения статистики по автомобилям и арендам
public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalCars, tvAvailableCars, tvTotalRentals, tvActiveRentals;
    private TextView tvCompletedRentals, tvTotalRevenue, tvAverageRentalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadStatistics();
    }

    // Инициализация текстовых полей
    private void initViews() {
        tvTotalCars = findViewById(R.id.tvTotalCars);
        tvAvailableCars = findViewById(R.id.tvAvailableCars);
        tvTotalRentals = findViewById(R.id.tvTotalRentals);
        tvActiveRentals = findViewById(R.id.tvActiveRentals);
        tvCompletedRentals = findViewById(R.id.tvCompletedRentals);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvAverageRentalPrice = findViewById(R.id.tvAverageRentalPrice);
    }

    // Загрузка и отображение статистических данных
    private void loadStatistics() {
        List<Car> allCars = dbHelper.getAllCars();
        List<Car> availableCars = dbHelper.getAvailableCars();
        List<Rental> allRentals = dbHelper.getAllRentals();
        List<Rental> activeRentals = dbHelper.getActiveRentals();

        int totalCars = allCars.size();
        int availableCarsCount = availableCars.size();
        int totalRentals = allRentals.size();
        int activeRentalsCount = activeRentals.size();

        int completedRentals = 0;
        double totalRevenue = 0;

        for (Rental rental : allRentals) {
            if ("completed".equals(rental.getStatus())) {
                completedRentals++;
                totalRevenue += rental.getTotalPrice();
            }
        }

        double averagePrice = completedRentals > 0 ? totalRevenue / completedRentals : 0;

        tvTotalCars.setText(String.format(Locale.getDefault(), "%d", totalCars));
        tvAvailableCars.setText(String.format(Locale.getDefault(), "%d", availableCarsCount));
        tvTotalRentals.setText(String.format(Locale.getDefault(), "%d", totalRentals));
        tvActiveRentals.setText(String.format(Locale.getDefault(), "%d", activeRentalsCount));
        tvCompletedRentals.setText(String.format(Locale.getDefault(), "%d", completedRentals));
        tvTotalRevenue.setText(String.format(Locale.getDefault(), "%.2f ₽", totalRevenue));
        tvAverageRentalPrice.setText(String.format(Locale.getDefault(), "%.2f ₽", averagePrice));
    }
}
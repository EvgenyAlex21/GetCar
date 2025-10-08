package com.example.avto_carshare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.service.NotificationService;

// Обработка истечения срока аренды
public class RentalExpirationReceiver extends BroadcastReceiver {
    private static final String TAG = "RentalExpirationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Получен сигнал об истечении аренды");

        int rentalId = intent.getIntExtra("rental_id", -1);
        int carId = intent.getIntExtra("car_id", -1);
        boolean isWarning = intent.getBooleanExtra("is_warning", false);

        if (rentalId == -1 || carId == -1) {
            Log.e(TAG, "Неверные параметры аренды");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        NotificationService notificationService = new NotificationService(context);

        try {
            Rental rental = getRentalById(dbHelper, rentalId);
            if (rental == null) {
                Log.e(TAG, "Аренда #" + rentalId + " не найдена");
                return;
            }

            Car car = dbHelper.getCarById(carId);

            if (isWarning) {
                // Предупреждение за 15 минут
                long minutesLeft = (rental.getEndTime() - System.currentTimeMillis()) / 60000;
                if (minutesLeft > 0) {
                    notificationService.sendRentalEndingSoonNotification(rental, car, minutesLeft);
                    Log.d(TAG, "Отправлено предупреждение для аренды #" + rentalId);
                }
            } else {
                // Окончание аренды с автоматическим завершением
                if ("active".equals(rental.getStatus())) {
                    dbHelper.updateRentalStatus(rentalId, "completed");
                    dbHelper.updateCarAvailability(carId, true);
                    dbHelper.decreaseCarFuelAfterRental(carId);
                    notificationService.sendRentalEndedNotification(rental, car);
                    Log.d(TAG, "Аренда #" + rentalId + " автоматически завершена");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обработке истечения аренды #" + rentalId, e);
        }
    }

    // Получение аренды по ID
    private Rental getRentalById(DatabaseHelper dbHelper, int rentalId) {
        for (Rental rental : dbHelper.getActiveRentals()) {
            if (rental.getId() == rentalId) {
                return rental;
            }
        }

        for (Rental rental : dbHelper.getAllRentals()) {
            if (rental.getId() == rentalId) {
                return rental;
            }
        }

        return null;
    }
}
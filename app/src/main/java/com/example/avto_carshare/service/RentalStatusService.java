package com.example.avto_carshare.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.receiver.RentalExpirationReceiver;
import java.util.List;

// Сервис для мониторинга статуса аренд и автоматического завершения
public class RentalStatusService {
    private static final String TAG = "RentalStatusService";
    private static final long CHECK_INTERVAL = 60000;
    private static final long WARNING_THRESHOLD = 15 * 60 * 1000;

    private Context context;
    private DatabaseHelper dbHelper;
    private NotificationService notificationService;
    private Handler handler;
    private Runnable monitoringRunnable;
    private boolean isMonitoring = false;

    public RentalStatusService(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.notificationService = new NotificationService(context);
        this.handler = new Handler(Looper.getMainLooper());
    }

    // Запуск и остановка мониторинга аренд
    public void startMonitoring() {
        if (isMonitoring) {
            return;
        }

        isMonitoring = true;
        Log.d(TAG, "Запуск мониторинга аренд");

        monitoringRunnable = new Runnable() {
            @Override
            public void run() {
                checkExpiredRentals();
                if (isMonitoring) {
                    handler.postDelayed(this, CHECK_INTERVAL);
                }
            }
        };

        handler.post(monitoringRunnable);
    }

    public void stopMonitoring() {
        isMonitoring = false;
        if (handler != null && monitoringRunnable != null) {
            handler.removeCallbacks(monitoringRunnable);
        }
        Log.d(TAG, "Остановка мониторинга аренд");
    }

    // Проверка истекших аренд
    private void checkExpiredRentals() {
        List<Rental> activeRentals = dbHelper.getActiveRentals();
        long currentTime = System.currentTimeMillis();

        Log.d(TAG, "Проверка аренд. Активных: " + activeRentals.size());

        for (Rental rental : activeRentals) {
            long endTime = rental.getEndTime();
            long timeLeft = endTime - currentTime;
            if (timeLeft <= 0) {
                Log.d(TAG, "Аренда #" + rental.getId() + " истекла. Автоматическое завершение.");
                completeRental(rental);
            }
            else if (timeLeft <= WARNING_THRESHOLD && timeLeft > 0) {
                long minutesLeft = timeLeft / 60000;
                Log.d(TAG, "Аренда #" + rental.getId() + " скоро закончится. Осталось " + minutesLeft + " минут.");
                sendWarningNotification(rental, minutesLeft);
            }
        }
    }

    // Автоматическое завершение аренды
    private void completeRental(Rental rental) {
        try {
            dbHelper.updateRentalStatus(rental.getId(), "completed");
            dbHelper.updateCarAvailability(rental.getCarId(), true);
            dbHelper.decreaseCarFuelAfterRental(rental.getCarId());
            Car car = dbHelper.getCarById(rental.getCarId());
            notificationService.sendRentalEndedNotification(rental, car);

            Log.d(TAG, "Аренда #" + rental.getId() + " успешно завершена автоматически");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при завершении аренды #" + rental.getId(), e);
        }
    }

    // Отправка предупреждения о скором окончании аренды
    private void sendWarningNotification(Rental rental, long minutesLeft) {
        try {
            Car car = dbHelper.getCarById(rental.getCarId());
            notificationService.sendRentalEndingSoonNotification(rental, car, minutesLeft);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при отправке предупреждения для аренды #" + rental.getId(), e);
        }
    }

    // Установка уведомления для конкретной аренды
    public void scheduleRentalExpiration(Rental rental) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RentalExpirationReceiver.class);
        intent.putExtra("rental_id", rental.getId());
        intent.putExtra("car_id", rental.getCarId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                rental.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        rental.getEndTime(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        rental.getEndTime(),
                        pendingIntent
                );
            }
            Log.d(TAG, "Запланировано завершение аренды #" + rental.getId());
        }
        scheduleWarningNotification(rental);
    }

    // Планирование предупреждения о скором окончании аренды
    private void scheduleWarningNotification(Rental rental) {
        long warningTime = rental.getEndTime() - WARNING_THRESHOLD;

        if (warningTime <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RentalExpirationReceiver.class);
        intent.putExtra("rental_id", rental.getId());
        intent.putExtra("car_id", rental.getCarId());
        intent.putExtra("is_warning", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                rental.getId() + 10000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        warningTime,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        warningTime,
                        pendingIntent
                );
            }
            Log.d(TAG, "Запланировано предупреждение для аренды #" + rental.getId());
        }
    }

    // Отмена запланированных уведомлений для аренды
    public void cancelScheduledAlarms(int rentalId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RentalExpirationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                rentalId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        PendingIntent warningPendingIntent = PendingIntent.getBroadcast(
                context,
                rentalId + 10000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(warningPendingIntent);
        }

        Log.d(TAG, "Отменены будильники для аренды #" + rentalId);
    }

    // Принудительная проверка всех аренд
    public void checkNow() {
        checkExpiredRentals();
    }
}
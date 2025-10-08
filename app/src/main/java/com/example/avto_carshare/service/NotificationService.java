package com.example.avto_carshare.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.avto_carshare.MainActivity;
import com.example.avto_carshare.R;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;

// Сервис для отправки уведомлений пользователю
public class NotificationService {
    private static final String CHANNEL_ID = "rental_notifications";
    private static final String CHANNEL_NAME = "Уведомления об аренде";
    private static final String CHANNEL_DESCRIPTION = "Уведомления о начале и окончании аренды";
    private static final int NOTIFICATION_ID_BASE = 1000;

    private Context context;
    private NotificationManager notificationManager;

    public NotificationService(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }


    // Создание канала уведомлений
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Отправка уведомления о начале аренды
    public void sendRentalStartedNotification(Rental rental, Car car) {
        String title = "Аренда началась";
        String message = String.format("Вы начали аренду автомобиля %s %s. Приятной поездки!",
                car != null ? car.getBrand() : "",
                car != null ? car.getModel() : "");

        sendNotification(rental.getId() + 20000, title, message);
    }

    // Отправка уведомления о скором окончании аренды
    public void sendRentalEndingSoonNotification(Rental rental, Car car, long minutesLeft) {
        String title = "Аренда скоро закончится";
        String message = String.format("Аренда автомобиля %s %s закончится через %d мин. Пожалуйста, верните автомобиль вовремя.",
                car != null ? car.getBrand() : "",
                car != null ? car.getModel() : "",
                minutesLeft);

        sendNotification(rental.getId() + 10000, title, message);
    }

    // Отправка уведомления об окончании аренды
    public void sendRentalEndedNotification(Rental rental, Car car) {
        String title = "Аренда завершена";
        String message = String.format("Аренда автомобиля %s %s завершена. Спасибо за использование нашего сервиса!",
                car != null ? car.getBrand() : "",
                car != null ? car.getModel() : "");

        sendNotification(rental.getId(), title, message);
    }

    // Отправка уведомления о новых доступных автомобилях
    public void sendNewCarsAvailableNotification() {
        String title = "Новый автомобиль добавлен";
        String message = "В каталоге появился новый автомобиль! Проверьте доступные варианты.";

        sendNotification(30000, title, message);
    }

    // Отправка общего уведомления
    private void sendNotification(int notificationId, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500});

        notificationManager.notify(notificationId, builder.build());
    }

    // Отмена уведомления
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
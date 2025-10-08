package com.example.avto_carshare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.avto_carshare.R;
import com.example.avto_carshare.model.Car;
import java.util.List;
import java.util.Locale;

// Адаптер для отображения списка автомобилей в RecyclerView
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private final Context context;
    private List<Car> carList;
    private final OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(Car car);
        void onRentClick(Car car);
    }

    public CarAdapter(Context context, List<Car> carList, OnCarClickListener listener) {
        this.context = context;
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);

        // Устанавливаем белый фон для карточки чтобы текст был читаемым
        holder.cardView.setCardBackgroundColor(Color.WHITE);

        holder.tvCarName.setText(context.getString(R.string.car_full_name, car.getBrand(), car.getModel()));
        holder.tvCarType.setText(context.getString(R.string.car_type_year, car.getCarType(), car.getYear()));

        // Отображение цвета с визуальным индикатором
        String colorName = getColorName(car.getColor());
        holder.tvCarDetails.setText(context.getString(R.string.car_color_seats, colorName, car.getSeats()));

        // Установление цветового индикатора
        if (holder.viewColorIndicator != null) {
            try {
                if (car.getColor().startsWith("#")) {
                    int color = Color.parseColor(car.getColor());
                    holder.viewColorIndicator.setBackgroundColor(color);
                } else {
                    int colorRes = getColorResourceByName(car.getColor().toLowerCase());
                    if (colorRes != 0) {
                        holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(context, colorRes));
                    } else {
                        holder.viewColorIndicator.setBackgroundColor(Color.GRAY);
                    }
                }
            } catch (Exception e) {
                holder.viewColorIndicator.setBackgroundColor(Color.GRAY);
            }
        }

        // Установка данных автомобиля в текстовые поля
        holder.tvLocation.setText(context.getString(R.string.car_location, car.getLocation()));
        holder.tvFuelLevel.setText(context.getString(R.string.car_fuel_level, car.getFuelLevel()));
        holder.tvPricePerHour.setText(String.format(Locale.getDefault(), "%.0f ₽/час", car.getPricePerHour()));
        holder.tvPricePerDay.setText(String.format(Locale.getDefault(), "%.0f ₽/день", car.getPricePerDay()));
        holder.tvLicensePlate.setText(context.getString(R.string.car_license_plate, car.getLicensePlate()));

        // Обработка доступности автомобиля
        if (car.isAvailable()) {
            holder.tvAvailability.setText(R.string.car_available);
            holder.tvAvailability.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tvAvailability.setBackgroundResource(R.drawable.bg_status_available);
            holder.btnRent.setEnabled(true);
            holder.btnRent.setAlpha(1.0f);
        } else {
            holder.tvAvailability.setText(R.string.car_unavailable);
            holder.tvAvailability.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tvAvailability.setBackgroundResource(R.drawable.bg_status_unavailable);
            holder.btnRent.setEnabled(false);
            holder.btnRent.setAlpha(0.5f);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCarClick(car);
            }
        });

        holder.btnRent.setOnClickListener(v -> {
            if (listener != null && car.isAvailable()) {
                listener.onRentClick(car);
            }
        });
    }

    // Список цветов автомобилей
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

    // Получение идентификатора ресурса цвета по имени
    private int getColorResourceByName(String colorName) {
        switch (colorName.toLowerCase()) {
            case "white":
            case "белый":
                return android.R.color.white;
            case "black":
            case "черный":
                return android.R.color.black;
            case "red":
            case "красный":
                return android.R.color.holo_red_dark;
            case "blue":
            case "синий":
                return android.R.color.holo_blue_dark;
            case "gray":
            case "grey":
            case "серый":
                return android.R.color.darker_gray;
            case "silver":
            case "серебристый":
                return android.R.color.secondary_text_light;
            case "orange":
            case "оранжевый":
                return android.R.color.holo_orange_dark;
            case "green":
            case "зеленый":
                return android.R.color.holo_green_dark;
            case "lightblue":
            case "голубой":
                return android.R.color.holo_blue_light;
            default:
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void updateData(List<Car> newCarList) {
        this.carList = newCarList;
        notifyDataSetChanged();
    }

    // ViewHolder для элементов списка
    public static class CarViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCarName, tvCarType, tvCarDetails, tvLocation, tvFuelLevel;
        TextView tvPricePerHour, tvPricePerDay, tvLicensePlate, tvAvailability;
        Button btnRent;
        View viewColorIndicator;

        // Инициализация view-элементов
        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarType = itemView.findViewById(R.id.tvCarType);
            tvCarDetails = itemView.findViewById(R.id.tvCarDetails);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvFuelLevel = itemView.findViewById(R.id.tvFuelLevel);
            tvPricePerHour = itemView.findViewById(R.id.tvPricePerHour);
            tvPricePerDay = itemView.findViewById(R.id.tvPricePerDay);
            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            btnRent = itemView.findViewById(R.id.btnRent);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
        }
    }
}
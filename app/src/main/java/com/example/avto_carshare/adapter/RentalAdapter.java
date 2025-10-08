package com.example.avto_carshare.adapter;

import android.content.Context;
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
import com.example.avto_carshare.database.DatabaseHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Адаптер для отображения списка аренд в RecyclerView
public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder> {

    private final Context context;
    private List<Rental> rentalList;
    private final DatabaseHelper dbHelper;
    private final OnRentalActionListener listener;

    public interface OnRentalActionListener {
        void onCompleteRental(Rental rental);
        void onCancelRental(Rental rental);
    }

    public RentalAdapter(Context context, List<Rental> rentalList, OnRentalActionListener listener) {
        this.context = context;
        this.rentalList = rentalList;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rental, parent, false);
        return new RentalViewHolder(view);
    }

    // Привязка данных к ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        Rental rental = rentalList.get(position);
        Car car = dbHelper.getCarById(rental.getCarId());
        User user = dbHelper.getUserById(rental.getUserId());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        if (car != null) {
            holder.tvCarInfo.setText(context.getString(R.string.rental_car_info,
                    car.getBrand(), car.getModel(), car.getLicensePlate()));
        } else {
            holder.tvCarInfo.setText(String.format(Locale.getDefault(), "Автомобиль #%d", rental.getCarId()));
        }

        if (user != null) {
            holder.tvUserInfo.setText(context.getString(R.string.rental_user_info,
                    user.getFullName(), user.getPhone() != null ? user.getPhone() : "Не указан"));
        } else {
            holder.tvUserInfo.setText("Пользователь");
        }

        holder.tvStartTime.setText(context.getString(R.string.rental_start_time,
                sdf.format(new Date(rental.getStartTime()))));
        holder.tvEndTime.setText(context.getString(R.string.rental_end_time,
                sdf.format(new Date(rental.getEndTime()))));
        holder.tvTotalPrice.setText(context.getString(R.string.rental_total_price,
                rental.getTotalPrice()));
        holder.tvPickupLocation.setText(context.getString(R.string.rental_pickup_location,
                rental.getPickupLocation()));

        // Обработка статуса аренды
        String status = rental.getStatus();
        switch (status) {
            case "active":
                holder.tvStatus.setText(R.string.rental_status_active);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available);
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "completed":
                holder.tvStatus.setText(R.string.rental_status_completed);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                holder.btnComplete.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                break;
            case "cancelled":
                holder.tvStatus.setText(R.string.rental_status_cancelled);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unavailable);
                holder.btnComplete.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                break;
        }

        holder.btnComplete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCompleteRental(rental);
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelRental(rental);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public void updateData(List<Rental> newRentalList) {
        this.rentalList = newRentalList;
        notifyDataSetChanged();
    }

    // ViewHolder для элементов списка
    public static class RentalViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCarInfo, tvUserInfo, tvStartTime, tvEndTime;
        TextView tvTotalPrice, tvStatus, tvPickupLocation;
        Button btnComplete, btnCancel;

        public RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCarInfo = itemView.findViewById(R.id.tvCarInfo);
            tvUserInfo = itemView.findViewById(R.id.tvUserInfo);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
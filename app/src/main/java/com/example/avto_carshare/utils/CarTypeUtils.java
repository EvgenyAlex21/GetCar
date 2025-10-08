package com.example.avto_carshare.utils;

import java.util.ArrayList;
import java.util.List;

// Типы автомобилей
public class CarTypeUtils {
    public static List<String> getCarTypes() {
        List<String> types = new ArrayList<>();
        types.add("Эконом");
        types.add("Комфорт");
        types.add("Премиум");
        types.add("Внедорожник");
        types.add("Минивэн");
        types.add("Хэтчбек");
        types.add("Седан");
        types.add("Кроссовер");
        types.add("Универсал");
        types.add("Купе");
        types.add("Кабриолет");
        return types;
    }

    public static boolean isValidCarType(String type) {
        return type != null && !type.trim().isEmpty() && getCarTypes().contains(type);
    }
}
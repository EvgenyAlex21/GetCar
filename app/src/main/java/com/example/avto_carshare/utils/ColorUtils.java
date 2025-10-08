package com.example.avto_carshare.utils;

import android.graphics.Color;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

// Цвета автомобилей
public class ColorUtils {
    public static class ColorItem {
        private final String name;
        private final String hexCode;

        public ColorItem(String name, String hexCode) {
            this.name = name;
            this.hexCode = hexCode;
        }

        public String getName() {
            return name;
        }

        public String getHexCode() {
            return hexCode;
        }

        public int getColorInt() {
            return Color.parseColor(hexCode);
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public static List<ColorItem> getAvailableColors() {
        List<ColorItem> colors = new ArrayList<>();
        colors.add(new ColorItem("Белый", "#FFFFFF"));
        colors.add(new ColorItem("Черный", "#000000"));
        colors.add(new ColorItem("Серый", "#808080"));
        colors.add(new ColorItem("Серебристый", "#C0C0C0"));
        colors.add(new ColorItem("Красный", "#FF0000"));
        colors.add(new ColorItem("Синий", "#0000FF"));
        colors.add(new ColorItem("Голубой", "#ADD8E6"));
        colors.add(new ColorItem("Зеленый", "#008000"));
        colors.add(new ColorItem("Желтый", "#FFFF00"));
        colors.add(new ColorItem("Оранжевый", "#FFA500"));
        colors.add(new ColorItem("Коричневый", "#8B4513"));
        colors.add(new ColorItem("Бежевый", "#F5F5DC"));
        return colors;
    }

    // Получение названия цвета по HEX коду и обратно
    public static String getColorName(String hexCode) {
        if (hexCode == null || hexCode.isEmpty()) {
            return "Неизвестный";
        }

        for (ColorItem color : getAvailableColors()) {
            if (color.getHexCode().equalsIgnoreCase(hexCode)) {
                return color.getName();
            }
        }
        return "Цветной";
    }

    public static String getColorHex(String colorName) {
        if (colorName == null || colorName.isEmpty()) {
            return "#808080";
        }

        for (ColorItem color : getAvailableColors()) {
            if (color.getName().equalsIgnoreCase(colorName)) {
                return color.getHexCode();
            }
        }
        return "#808080";
    }
}
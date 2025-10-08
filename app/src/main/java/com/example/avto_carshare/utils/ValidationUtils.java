package com.example.avto_carshare.utils;

import java.util.regex.Pattern;

// Проверка формата госномера РФ
public class ValidationUtils {
    private static final String ALLOWED_LETTERS = "АВЕКМНОРСТУХABEKMHOPCTYX";
    public static boolean isValidLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        licensePlate = licensePlate.trim().toUpperCase();
        if (licensePlate.length() < 8 || licensePlate.length() > 9) {
            return false;
        }
        if (!isAllowedLetter(licensePlate.charAt(0))) {
            return false;
        }
        for (int i = 1; i <= 3; i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                return false;
            }
        }
        if (!isAllowedLetter(licensePlate.charAt(4)) || !isAllowedLetter(licensePlate.charAt(5))) {
            return false;
        }

        int regionLength = licensePlate.length() - 6;
        if (regionLength < 2 || regionLength > 3) {
            return false;
        }

        for (int i = 6; i < licensePlate.length(); i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isAllowedLetter(char c) {
        return ALLOWED_LETTERS.indexOf(Character.toUpperCase(c)) >= 0;
    }

    // Проверка формата номеров телефонов РФ
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        phone = phone.trim();
        String cleanPhone = phone.replaceAll("[^0-9+]", "");
        String digitsOnly = cleanPhone.replace("+", "");
        if (digitsOnly.length() == 0 || digitsOnly.length() > 12) {
            return false;
        }
        if (cleanPhone.startsWith("+")) {
            return digitsOnly.length() >= 10 && digitsOnly.length() <= 12;
        } else if (cleanPhone.startsWith("8") || cleanPhone.startsWith("7")) {
            return digitsOnly.length() >= 10 && digitsOnly.length() <= 12;
        }
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 12;
    }

    // Проверка формата водительского удостоверения РФ
    public static boolean isValidDriverLicense(String license) {
        if (license == null || license.trim().isEmpty()) {
            return false;
        }
        license = license.trim();
        String digitsOnly = license.replaceAll("[^0-9]", "");
        return digitsOnly.length() > 0 && digitsOnly.length() <= 10 && digitsOnly.equals(license);
    }

    // Проверка формата года автомобиля
    public static boolean isValidYear(int year) {
        return year >= 1886 && year <= 3000;
    }
    public static boolean isValidYear(String yearStr) {
        if (yearStr == null || yearStr.trim().isEmpty()) {
            return false;
        }

        try {
            int year = Integer.parseInt(yearStr.trim());
            return isValidYear(year);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка количества мест в автомобиле
    public static boolean isValidSeats(int seats) {
        return seats > 0 && seats <= 50;
    }
    public static boolean isValidSeats(String seatsStr) {
        if (seatsStr == null || seatsStr.trim().isEmpty()) {
            return false;
        }
        try {
            int seats = Integer.parseInt(seatsStr.trim());
            return isValidSeats(seats);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка формата пользовательских данных (email, password, name)
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.compile(emailPattern).matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Нормализация номера телефона в +7XXXXXXXXXX
    public static String normalizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        if (digitsOnly.startsWith("8") && digitsOnly.length() == 11) {
            digitsOnly = "7" + digitsOnly.substring(1);
        }
        if (!digitsOnly.startsWith("7") && digitsOnly.length() == 10) {
            digitsOnly = "7" + digitsOnly;
        }
        return "+" + digitsOnly;
    }

    public static String formatPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }

        String digitsOnly = phone.replaceAll("[^0-9]", "");

        if (digitsOnly.length() == 11 && digitsOnly.startsWith("7")) {
            return String.format("+7 (%s) %s-%s-%s",
                    digitsOnly.substring(1, 4),
                    digitsOnly.substring(4, 7),
                    digitsOnly.substring(7, 9),
                    digitsOnly.substring(9, 11));
        } else if (digitsOnly.length() == 11 && digitsOnly.startsWith("8")) {
            return String.format("8 (%s) %s-%s-%s",
                    digitsOnly.substring(1, 4),
                    digitsOnly.substring(4, 7),
                    digitsOnly.substring(7, 9),
                    digitsOnly.substring(9, 11));
        }
        return phone;
    }

    // Форматирование госномера для отображения - А123AA777
    public static String formatLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.isEmpty()) {
            return licensePlate;
        }
        licensePlate = licensePlate.trim().toUpperCase();
        if (licensePlate.length() >= 8 && licensePlate.length() <= 9) {
            String letter1 = licensePlate.substring(0, 1);
            String digits1 = licensePlate.substring(1, 4);
            String letters2 = licensePlate.substring(4, 6);
            String region = licensePlate.substring(6);

            return letter1 + " " + digits1 + " " + letters2 + " " + region;
        }
        return licensePlate;
    }

    // Проверка полного цвета автомобиля с HEX кодом
    public static boolean isValidColorHex(String color) {
        if (color == null || color.trim().isEmpty()) {
            return false;
        }
        return color.matches("^#([A-Fa-f0-9]{6})$");
    }

    // Комплексная валидация данных автомобиля
    public static String validateCarData(com.example.avto_carshare.model.Car car) {
        if (car == null) {
            return "Данные автомобиля отсутствуют";
        }
        if (!isValidLicensePlate(car.getLicensePlate())) {
            return "Неверный формат госномера. Пример: А123БВ77 или А123БВ777";
        }
        if (!isValidYear(car.getYear())) {
            return "Год должен быть от 1886 до 3000";
        }
        if (!isValidSeats(car.getSeats())) {
            return "Количество мест должно быть от 1 до 50";
        }
        if (car.getColor() != null && !car.getColor().isEmpty() &&
                car.getColor().startsWith("#") && !isValidColorHex(car.getColor())) {
            return "Неверный формат цвета";
        }
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            return "Укажите марку автомобиля";
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            return "Укажите модель автомобиля";
        }
        if (car.getPricePerHour() <= 0) {
            return "Цена за час должна быть больше 0";
        }
        if (car.getPricePerDay() <= 0) {
            return "Цена за день должна быть больше 0";
        }
        if (car.getFuelLevel() < 0 || car.getFuelLevel() > 100) {
            return "Уровень топлива должен быть от 0 до 100%";
        }
        return null;
    }

    // Получение списка допустимых букв для госномеров
    public static String getAllowedLetters() {
        return ALLOWED_LETTERS;
    }
}
package com.example.avto_carshare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.avto_carshare.model.Car;
import com.example.avto_carshare.model.Rental;
import com.example.avto_carshare.model.User;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "carsharing.db";
    private static final int DATABASE_VERSION = 4;

    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String USER_ID = "id";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FULL_NAME = "full_name";
    private static final String USER_PHONE = "phone";
    private static final String USER_DRIVER_LICENSE = "driver_license";
    private static final String USER_REGISTRATION_DATE = "registration_date";
    private static final String USER_PROFILE_IMAGE = "profile_image";

    // Таблица автомобилей
    private static final String TABLE_CARS = "cars";
    private static final String CAR_ID = "id";
    private static final String CAR_BRAND = "brand";
    private static final String CAR_MODEL = "model";
    private static final String CAR_LICENSE_PLATE = "license_plate";
    private static final String CAR_TYPE = "car_type";
    private static final String CAR_YEAR = "year";
    private static final String CAR_COLOR = "color";
    private static final String CAR_PRICE_PER_HOUR = "price_per_hour";
    private static final String CAR_PRICE_PER_DAY = "price_per_day";
    private static final String CAR_IS_AVAILABLE = "is_available";
    private static final String CAR_IMAGE_URL = "image_url";
    private static final String CAR_LOCATION = "location";
    private static final String CAR_FUEL_LEVEL = "fuel_level";
    private static final String CAR_SEATS = "seats";

    // Таблица аренды
    private static final String TABLE_RENTALS = "rentals";
    private static final String RENTAL_ID = "id";
    private static final String RENTAL_CAR_ID = "car_id";
    private static final String RENTAL_USER_ID = "user_id";
    private static final String RENTAL_START_TIME = "start_time";
    private static final String RENTAL_END_TIME = "end_time";
    private static final String RENTAL_TOTAL_PRICE = "total_price";
    private static final String RENTAL_STATUS = "status";
    private static final String RENTAL_PICKUP_LOCATION = "pickup_location";
    private static final String RENTAL_RETURN_LOCATION = "return_location";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                USER_PASSWORD + " TEXT NOT NULL, " +
                USER_FULL_NAME + " TEXT, " +
                USER_PHONE + " TEXT, " +
                USER_DRIVER_LICENSE + " TEXT, " +
                USER_REGISTRATION_DATE + " INTEGER, " +
                USER_PROFILE_IMAGE + " TEXT)";
        db.execSQL(createUsersTable);

        // Создание таблицы автомобилей
        String createCarsTable = "CREATE TABLE " + TABLE_CARS + " (" +
                CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CAR_BRAND + " TEXT NOT NULL, " +
                CAR_MODEL + " TEXT NOT NULL, " +
                CAR_LICENSE_PLATE + " TEXT UNIQUE NOT NULL, " +
                CAR_TYPE + " TEXT, " +
                CAR_YEAR + " INTEGER, " +
                CAR_COLOR + " TEXT, " +
                CAR_PRICE_PER_HOUR + " REAL, " +
                CAR_PRICE_PER_DAY + " REAL, " +
                CAR_IS_AVAILABLE + " INTEGER DEFAULT 1, " +
                CAR_IMAGE_URL + " TEXT, " +
                CAR_LOCATION + " TEXT, " +
                CAR_FUEL_LEVEL + " REAL, " +
                CAR_SEATS + " INTEGER)";
        db.execSQL(createCarsTable);

        // Создание таблицы аренды
        String createRentalsTable = "CREATE TABLE " + TABLE_RENTALS + " (" +
                RENTAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RENTAL_CAR_ID + " INTEGER, " +
                RENTAL_USER_ID + " INTEGER, " +
                RENTAL_START_TIME + " INTEGER, " +
                RENTAL_END_TIME + " INTEGER, " +
                RENTAL_TOTAL_PRICE + " REAL, " +
                RENTAL_STATUS + " TEXT, " +
                RENTAL_PICKUP_LOCATION + " TEXT, " +
                RENTAL_RETURN_LOCATION + " TEXT, " +
                "FOREIGN KEY(" + RENTAL_CAR_ID + ") REFERENCES " + TABLE_CARS + "(" + CAR_ID + "), " +
                "FOREIGN KEY(" + RENTAL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";
        db.execSQL(createRentalsTable);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RENTALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Добавление тестовых автомобилей
    private void insertSampleData(SQLiteDatabase db) {
        // Эконом класс (10 автомобилей)
        insertSampleCar(db, "Kia", "Rio", "В456ОМ199", "Эконом", 2021, "#FFFFFF", 250, 1800, true, "", "Москва, ул. Арбат 10", 92.0, 5);
        insertSampleCar(db, "Hyundai", "Solaris", "Е012НК197", "Эконом", 2020, "#0000FF", 230, 1600, true, "", "Москва, ул. Ленина 15", 88.0, 5);
        insertSampleCar(db, "Volkswagen", "Polo", "К678МН197", "Эконом", 2021, "#FF0000", 270, 1900, true, "", "Москва, пр. Мира 30", 95.0, 5);
        insertSampleCar(db, "Renault", "Logan", "М234РС799", "Эконом", 2020, "#C0C0C0", 220, 1500, true, "", "Москва, ул. Горького 12", 85.0, 5);
        insertSampleCar(db, "Lada", "Vesta", "Н567СТ177", "Эконом", 2022, "#FFFFFF", 200, 1400, true, "", "Москва, ул. Тверская 5", 90.0, 5);
        insertSampleCar(db, "Chevrolet", "Aveo", "О890УХ799", "Эконом", 2019, "#000000", 210, 1450, true, "", "Москва, ул. Чехова 20", 87.0, 5);
        insertSampleCar(db, "Skoda", "Rapid", "Р123ХУ197", "Эконом", 2021, "#ADD8E6", 260, 1850, true, "", "Москва, ул. Пушкина 8", 93.0, 5);
        insertSampleCar(db, "Ford", "Focus", "С456ЕК777", "Эконом", 2020, "#808080", 280, 1950, true, "", "Москва, пр. Ленина 40", 89.0, 5);
        insertSampleCar(db, "Opel", "Astra", "Т789ВН199", "Эконом", 2021, "#FFFFFF", 270, 1900, true, "", "Москва, ул. Маяковского 18", 91.0, 5);
        insertSampleCar(db, "Nissan", "Almera", "У012МО177", "Эконом", 2020, "#C0C0C0", 240, 1700, true, "", "Москва, ул. Гоголя 25", 86.0, 5);

        // Комфорт класс (10 автомобилей)
        insertSampleCar(db, "Toyota", "Camry", "А123ВК777", "Комфорт", 2022, "#000000", 400, 3000, true, "", "Москва, ул. Тверская 1", 85.0, 5);
        insertSampleCar(db, "Mazda", "6", "В345ЕН197", "Комфорт", 2021, "#FF0000", 380, 2800, true, "", "Москва, ул. Кутузова 15", 88.0, 5);
        insertSampleCar(db, "Honda", "Accord", "Е678КО777", "Комфорт", 2022, "#FFFFFF", 420, 3100, true, "", "Москва, пр. Вернадского 30", 90.0, 5);
        insertSampleCar(db, "Volkswagen", "Passat", "К901МР199", "Комфорт", 2021, "#0000FF", 390, 2900, true, "", "Москва, ул. Новый Арбат 12", 87.0, 5);
        insertSampleCar(db, "Skoda", "Octavia", "М234НС177", "Комфорт", 2022, "#808080", 370, 2700, true, "", "Москва, ул. Баумана 22", 92.0, 5);
        insertSampleCar(db, "Toyota", "RAV4", "Н567ОТ799", "Комфорт", 2023, "#000000", 450, 3300, true, "", "Москва, Ленинский пр. 45", 89.0, 5);
        insertSampleCar(db, "Hyundai", "Sonata", "О890РУ197", "Комфорт", 2021, "#C0C0C0", 380, 2850, true, "", "Москва, ул. Садовая 8", 86.0, 5);
        insertSampleCar(db, "Kia", "K5", "Р123СХ777", "Комфорт", 2022, "#FFFFFF", 400, 3000, true, "", "Москва, пр. Мира 60", 91.0, 5);
        insertSampleCar(db, "Nissan", "Teana", "С456ТА199", "Комфорт", 2020, "#000000", 360, 2700, true, "", "Москва, ул. Тимирязева 35", 84.0, 5);
        insertSampleCar(db, "Mazda", "CX-5", "Т789УВ177", "Комфорт", 2023, "#FF0000", 430, 3200, true, "", "Москва, Кутузовский пр. 28", 93.0, 5);

        // Премиум класс (10 автомобилей)
        insertSampleCar(db, "BMW", "X5", "У789ХЕ177", "Премиум", 2023, "#808080", 800, 6000, true, "", "Москва, Кутузовский пр. 5", 78.0, 7);
        insertSampleCar(db, "Mercedes-Benz", "E-Class", "Х345КМ799", "Премиум", 2023, "#000000", 900, 6500, true, "", "Москва, ул. Садовая 20", 82.0, 5);
        insertSampleCar(db, "Audi", "Q7", "А901ОН177", "Премиум", 2022, "#FFFFFF", 850, 6200, true, "", "Москва, ул. Пушкина 8", 80.0, 7);
        insertSampleCar(db, "BMW", "5 Series", "В012РО799", "Премиум", 2023, "#000000", 850, 6300, true, "", "Москва, ул. Остоженка 10", 85.0, 5);
        insertSampleCar(db, "Mercedes-Benz", "GLE", "Е345СР197", "Премиум", 2023, "#C0C0C0", 950, 7000, true, "", "Москва, Рублевское ш. 15", 79.0, 7);
        insertSampleCar(db, "Audi", "A6", "К678ТС777", "Премиум", 2022, "#0000FF", 780, 5800, true, "", "Москва, ул. Пречистенка 25", 83.0, 5);
        insertSampleCar(db, "Lexus", "RX", "М901УТ199", "Премиум", 2023, "#FFFFFF", 920, 6800, true, "", "Москва, ул. Воздвиженка 12", 86.0, 5);
        insertSampleCar(db, "Porsche", "Cayenne", "Н234ХУ177", "Премиум", 2023, "#000000", 1200, 9000, true, "", "Москва, Ленинградский пр. 30", 75.0, 5);
        insertSampleCar(db, "BMW", "X7", "О567АВ799", "Премиум", 2023, "#808080", 1100, 8500, true, "", "Москва, пр. Вернадского 55", 77.0, 7);
        insertSampleCar(db, "Mercedes-Benz", "S-Class", "Р890ВЕ197", "Премиум", 2024, "#000000", 1500, 11000, true, "", "Москва, ул. Тверская 35", 88.0, 5);

        // Внедорожники (7 автомобилей)
        insertSampleCar(db, "Toyota", "Land Cruiser", "С123ЕК199", "Внедорожник", 2022, "#FFFFFF", 1000, 7500, true, "", "Москва, Ленинский пр. 80", 81.0, 7);
        insertSampleCar(db, "Nissan", "Patrol", "Т456КМ777", "Внедорожник", 2021, "#000000", 850, 6400, true, "", "Москва, МКАД 108 км", 76.0, 7);
        insertSampleCar(db, "Jeep", "Wrangler", "У789МН799", "Внедорожник", 2022, "#008000", 900, 6700, true, "", "Москва, Дмитровское ш. 25", 74.0, 5);
        insertSampleCar(db, "Land Rover", "Discovery", "Х012НО799", "Внедорожник", 2023, "#808080", 1050, 7800, true, "", "Москва, Рублевское ш. 40", 79.0, 7);
        insertSampleCar(db, "Mitsubishi", "Pajero", "А345ОР197", "Внедорожник", 2020, "#000000", 700, 5200, true, "", "Москва, Варшавское ш. 50", 72.0, 7);
        insertSampleCar(db, "Toyota", "Fortuner", "В678РС777", "Внедорожник", 2023, "#FFFFFF", 850, 6300, true, "", "Москва, Каширское ш. 70", 77.0, 7);
        insertSampleCar(db, "Ford", "Explorer", "Е901СТ199", "Внедорожник", 2022, "#0000FF", 920, 6800, true, "", "Москва, пр. Мира 90", 79.0, 7);

        // Минивэны (6 автомобилей)
        insertSampleCar(db, "Volkswagen", "Multivan", "К678ТУ197", "Минивэн", 2022, "#0000FF", 600, 4500, true, "", "Москва, ул. Профсоюзная 65", 87.0, 7);
        insertSampleCar(db, "Mercedes-Benz", "V-Class", "М901УХ777", "Минивэн", 2023, "#000000", 900, 6700, true, "", "Москва, пр. Мира 120", 84.0, 8);
        insertSampleCar(db, "Toyota", "Alphard", "Н234ХА799", "Минивэн", 2022, "#FFFFFF", 950, 7000, true, "", "Москва, Кутузовский пр. 48", 86.0, 7);
        insertSampleCar(db, "Ford", "Transit", "О567АВ177", "Минивэн", 2021, "#C0C0C0", 500, 3800, true, "", "Москва, Волгоградский пр. 32", 82.0, 9);
        insertSampleCar(db, "Hyundai", "H1", "Р890ВЕ777", "Минивэн", 2020, "#808080", 450, 3400, true, "", "Москва, ул. Большая Черемушкинская 20", 80.0, 8);
        insertSampleCar(db, "Citroen", "SpaceTourer", "С123ЕК177", "Минивэн", 2021, "#FFFFFF", 550, 4200, true, "", "Москва, ул. Ленинградская 55", 85.0, 8);

        // Хэтчбеки (6 автомобилей)
        insertSampleCar(db, "Volkswagen", "Golf", "Т123КМ777", "Хэтчбек", 2021, "#FF0000", 320, 2300, true, "", "Москва, ул. Новый Арбат 18", 91.0, 5);
        insertSampleCar(db, "Mazda", "3", "У456МН199", "Хэтчбек", 2022, "#0000FF", 340, 2500, true, "", "Москва, пр. Андропова 28", 89.0, 5);
        insertSampleCar(db, "Toyota", "Corolla", "Х789НО177", "Хэтчбек", 2021, "#C0C0C0", 330, 2400, true, "", "Москва, ул. Вавилова 45", 90.0, 5);
        insertSampleCar(db, "Skoda", "Scala", "А012ОР799", "Хэтчбек", 2022, "#FFFFFF", 310, 2250, true, "", "Москва, Ленинградское ш. 72", 88.0, 5);
        insertSampleCar(db, "Honda", "Civic", "В345РС197", "Хэтчбек", 2023, "#000000", 360, 2700, true, "", "Москва, пр. Вернадского 90", 92.0, 5);
        insertSampleCar(db, "Peugeot", "308", "Е678СТ777", "Хэтчбек", 2021, "#FF0000", 330, 2400, true, "", "Москва, ул. Профсоюзная 100", 89.0, 5);

        // Кроссоверы (7 автомобилей)
        insertSampleCar(db, "Hyundai", "Tucson", "К678ТУ999", "Кроссовер", 2022, "#808080", 450, 3300, true, "", "Москва, ул. Профсоюзная 120", 86.0, 5);
        insertSampleCar(db, "Kia", "Sportage", "М901УХ197", "Кроссовер", 2023, "#FFFFFF", 470, 3450, true, "", "Москва, Каширское ш. 55", 88.0, 5);
        insertSampleCar(db, "Nissan", "Qashqai", "Н234ХА197", "Кроссовер", 2022, "#FF0000", 420, 3100, true, "", "Москва, ул. Новослободская 38", 87.0, 5);
        insertSampleCar(db, "Volkswagen", "Tiguan", "О567АВ197", "Кроссовер", 2023, "#0000FF", 480, 3550, true, "", "Москва, Ленинский пр. 140", 89.0, 5);
        insertSampleCar(db, "Renault", "Arkana", "Р890ВЕ199", "Кроссовер", 2022, "#FFA500", 400, 2950, true, "", "Москва, ул. Академика Королева 12", 85.0, 5);
        insertSampleCar(db, "Mitsubishi", "Outlander", "С123ЕК797", "Кроссовер", 2021, "#000000", 430, 3200, true, "", "Москва, пр. Мира 85", 86.0, 5);
        insertSampleCar(db, "Mazda", "CX-30", "Т456КМ197", "Кроссовер", 2023, "#C0C0C0", 440, 3250, true, "", "Москва, ул. Тверская 75", 88.0, 5);

        // Седаны (4 автомобиля)
        insertSampleCar(db, "BMW", "3 Series", "У789МН197", "Седан", 2022, "#000000", 550, 4100, true, "", "Москва, ул. Садовая 45", 85.0, 5);
        insertSampleCar(db, "Audi", "A4", "Х012НО197", "Седан", 2023, "#FFFFFF", 530, 3950, true, "", "Москва, пр. Вернадского 70", 87.0, 5);
        insertSampleCar(db, "Mercedes-Benz", "C-Class", "А345ОР777", "Седан", 2023, "#808080", 580, 4300, true, "", "Москва, Кутузовский пр. 35", 86.0, 5);
        insertSampleCar(db, "Lexus", "ES", "В678РС999", "Седан", 2022, "#000000", 600, 4500, true, "", "Москва, ул. Остоженка 22", 88.0, 5);
    }

    private void insertSampleCar(SQLiteDatabase db, String brand, String model, String licensePlate,
                                 String type, int year, String color, double pricePerHour,
                                 double pricePerDay, boolean isAvailable, String imageUrl,
                                 String location, double fuelLevel, int seats) {
        ContentValues values = new ContentValues();
        values.put(CAR_BRAND, brand);
        values.put(CAR_MODEL, model);
        values.put(CAR_LICENSE_PLATE, licensePlate);
        values.put(CAR_TYPE, type);
        values.put(CAR_YEAR, year);
        values.put(CAR_COLOR, color);
        values.put(CAR_PRICE_PER_HOUR, pricePerHour);
        values.put(CAR_PRICE_PER_DAY, pricePerDay);
        values.put(CAR_IS_AVAILABLE, isAvailable ? 1 : 0);
        values.put(CAR_IMAGE_URL, imageUrl);
        values.put(CAR_LOCATION, location);
        values.put(CAR_FUEL_LEVEL, fuelLevel);
        values.put(CAR_SEATS, seats);
        db.insert(TABLE_CARS, null, values);
    }

    // Добавление автомобиля
    public long addCar(Car car) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAR_BRAND, car.getBrand());
        values.put(CAR_MODEL, car.getModel());
        values.put(CAR_LICENSE_PLATE, car.getLicensePlate());
        values.put(CAR_TYPE, car.getCarType());
        values.put(CAR_YEAR, car.getYear());
        values.put(CAR_COLOR, car.getColor());
        values.put(CAR_PRICE_PER_HOUR, car.getPricePerHour());
        values.put(CAR_PRICE_PER_DAY, car.getPricePerDay());
        values.put(CAR_IS_AVAILABLE, car.isAvailable() ? 1 : 0);
        values.put(CAR_IMAGE_URL, car.getImageUrl());
        values.put(CAR_LOCATION, car.getLocation());
        values.put(CAR_FUEL_LEVEL, car.getFuelLevel());
        values.put(CAR_SEATS, car.getSeats());

        long id = db.insert(TABLE_CARS, null, values);
        db.close();
        return id;
    }

    // Получение всех автомобилей
    public List<Car> getAllCars() {
        List<Car> carList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CARS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_ID)));
                car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(CAR_BRAND)));
                car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(CAR_MODEL)));
                car.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LICENSE_PLATE)));
                car.setCarType(cursor.getString(cursor.getColumnIndexOrThrow(CAR_TYPE)));
                car.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_YEAR)));
                car.setColor(cursor.getString(cursor.getColumnIndexOrThrow(CAR_COLOR)));
                car.setPricePerHour(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_HOUR)));
                car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_DAY)));
                car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_IS_AVAILABLE)) == 1);
                car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(CAR_IMAGE_URL)));
                car.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LOCATION)));
                car.setFuelLevel(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_FUEL_LEVEL)));
                car.setSeats(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_SEATS)));
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }

    // Получение доступных автомобилей
    public List<Car> getAvailableCars() {
        List<Car> carList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CARS + " WHERE " + CAR_IS_AVAILABLE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_ID)));
                car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(CAR_BRAND)));
                car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(CAR_MODEL)));
                car.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LICENSE_PLATE)));
                car.setCarType(cursor.getString(cursor.getColumnIndexOrThrow(CAR_TYPE)));
                car.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_YEAR)));
                car.setColor(cursor.getString(cursor.getColumnIndexOrThrow(CAR_COLOR)));
                car.setPricePerHour(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_HOUR)));
                car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_DAY)));
                car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_IS_AVAILABLE)) == 1);
                car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(CAR_IMAGE_URL)));
                car.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LOCATION)));
                car.setFuelLevel(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_FUEL_LEVEL)));
                car.setSeats(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_SEATS)));
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }

    // Получение автомобиля по ID
    public Car getCarById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARS, null, CAR_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Car car = new Car();
            car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_ID)));
            car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(CAR_BRAND)));
            car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(CAR_MODEL)));
            car.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LICENSE_PLATE)));
            car.setCarType(cursor.getString(cursor.getColumnIndexOrThrow(CAR_TYPE)));
            car.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_YEAR)));
            car.setColor(cursor.getString(cursor.getColumnIndexOrThrow(CAR_COLOR)));
            car.setPricePerHour(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_HOUR)));
            car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_DAY)));
            car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_IS_AVAILABLE)) == 1);
            car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(CAR_IMAGE_URL)));
            car.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LOCATION)));
            car.setFuelLevel(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_FUEL_LEVEL)));
            car.setSeats(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_SEATS)));
            cursor.close();
            db.close();
            return car;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Обновление автомобиля
    public int updateCar(Car car) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAR_BRAND, car.getBrand());
        values.put(CAR_MODEL, car.getModel());
        values.put(CAR_LICENSE_PLATE, car.getLicensePlate());
        values.put(CAR_TYPE, car.getCarType());
        values.put(CAR_YEAR, car.getYear());
        values.put(CAR_COLOR, car.getColor());
        values.put(CAR_PRICE_PER_HOUR, car.getPricePerHour());
        values.put(CAR_PRICE_PER_DAY, car.getPricePerDay());
        values.put(CAR_IS_AVAILABLE, car.isAvailable() ? 1 : 0);
        values.put(CAR_IMAGE_URL, car.getImageUrl());
        values.put(CAR_LOCATION, car.getLocation());
        values.put(CAR_FUEL_LEVEL, car.getFuelLevel());
        values.put(CAR_SEATS, car.getSeats());

        int result = db.update(TABLE_CARS, values, CAR_ID + "=?",
                new String[]{String.valueOf(car.getId())});
        db.close();
        return result;
    }

    // Удаление автомобиля
    public void deleteCar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARS, CAR_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Добавление аренды
    public long addRental(Rental rental) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RENTAL_CAR_ID, rental.getCarId());
        values.put(RENTAL_USER_ID, rental.getUserId());
        values.put(RENTAL_START_TIME, rental.getStartTime());
        values.put(RENTAL_END_TIME, rental.getEndTime());
        values.put(RENTAL_TOTAL_PRICE, rental.getTotalPrice());
        values.put(RENTAL_STATUS, rental.getStatus());
        values.put(RENTAL_PICKUP_LOCATION, rental.getPickupLocation());
        values.put(RENTAL_RETURN_LOCATION, rental.getReturnLocation());

        long id = db.insert(TABLE_RENTALS, null, values);
        db.close();
        return id;
    }

    // Получение всех аренд
    public List<Rental> getAllRentals() {
        List<Rental> rentalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS + " ORDER BY " + RENTAL_START_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Rental rental = new Rental();
                rental.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_ID)));
                rental.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_CAR_ID)));
                rental.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_USER_ID)));
                rental.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME)));
                rental.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME)));
                rental.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RENTAL_TOTAL_PRICE)));
                rental.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_STATUS)));
                rental.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_PICKUP_LOCATION)));
                rental.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_RETURN_LOCATION)));
                rentalList.add(rental);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rentalList;
    }

    // Получение активных аренд
    public List<Rental> getActiveRentals() {
        List<Rental> rentalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS + " WHERE " + RENTAL_STATUS + " = 'active'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Rental rental = new Rental();
                rental.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_ID)));
                rental.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_CAR_ID)));
                rental.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_USER_ID)));
                rental.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME)));
                rental.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME)));
                rental.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RENTAL_TOTAL_PRICE)));
                rental.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_STATUS)));
                rental.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_PICKUP_LOCATION)));
                rental.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_RETURN_LOCATION)));
                rentalList.add(rental);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rentalList;
    }

    // Обновление статуса аренды
    public int updateRentalStatus(int rentalId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RENTAL_STATUS, status);

        int result = db.update(TABLE_RENTALS, values, RENTAL_ID + "=?",
                new String[]{String.valueOf(rentalId)});
        db.close();
        return result;
    }

    // Обновление доступности автомобиля
    public void updateCarAvailability(int carId, boolean isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAR_IS_AVAILABLE, isAvailable ? 1 : 0);
        db.update(TABLE_CARS, values, CAR_ID + "=?", new String[]{String.valueOf(carId)});
        db.close();
    }

    // Обновление уровня топлива автомобиля
    public void updateCarFuelLevel(int carId, double newFuelLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAR_FUEL_LEVEL, newFuelLevel);
        db.update(TABLE_CARS, values, CAR_ID + "=?", new String[]{String.valueOf(carId)});
        db.close();
    }

    // Уменьшение уровня топлива после аренды в зависимости от длительности
    public void decreaseCarFuelAfterRental(int carId) {
        Car car = getCarById(carId);
        if (car != null) {
            double currentFuel = car.getFuelLevel();

            String query = "SELECT * FROM " + TABLE_RENTALS +
                          " WHERE " + RENTAL_CAR_ID + " = ? AND " +
                          RENTAL_STATUS + " = 'completed' ORDER BY " +
                          RENTAL_END_TIME + " DESC LIMIT 1";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(carId)});

            double fuelDecrease = 0;

            if (cursor != null && cursor.moveToFirst()) {
                long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME));
                long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME));

                long durationMinutes = (endTime - startTime) / (60 * 1000);

                if (durationMinutes < 10) {
                    fuelDecrease = 1;
                }
                else if (durationMinutes < 30) {
                    fuelDecrease = 2 + (Math.random() * 2);
                }
                else if (durationMinutes < 60) {
                    fuelDecrease = 3 + (Math.random() * 3);
                }
                else if (durationMinutes < 180) {
                    fuelDecrease = 5 + (Math.random() * 5);
                }
                else if (durationMinutes < 720) {
                    fuelDecrease = 10 + (Math.random() * 5);
                }
                else {
                    fuelDecrease = 15 + (Math.random() * 5);
                }
            } else {
                fuelDecrease = 1;
            }

            if (cursor != null) cursor.close();

            double newFuelLevel = currentFuel - fuelDecrease;

            if (newFuelLevel < 1) {
                newFuelLevel = 1;
            }

            updateCarFuelLevel(carId, newFuelLevel);
        }
    }

    // Поиск автомобилей по типу
    public List<Car> searchCarsByType(String type) {
        List<Car> carList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CARS + " WHERE " + CAR_TYPE + " = ? AND " + CAR_IS_AVAILABLE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type});

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_ID)));
                car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(CAR_BRAND)));
                car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(CAR_MODEL)));
                car.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LICENSE_PLATE)));
                car.setCarType(cursor.getString(cursor.getColumnIndexOrThrow(CAR_TYPE)));
                car.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_YEAR)));
                car.setColor(cursor.getString(cursor.getColumnIndexOrThrow(CAR_COLOR)));
                car.setPricePerHour(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_HOUR)));
                car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_PRICE_PER_DAY)));
                car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_IS_AVAILABLE)) == 1);
                car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(CAR_IMAGE_URL)));
                car.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(CAR_LOCATION)));
                car.setFuelLevel(cursor.getDouble(cursor.getColumnIndexOrThrow(CAR_FUEL_LEVEL)));
                car.setSeats(cursor.getInt(cursor.getColumnIndexOrThrow(CAR_SEATS)));
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }

    // Регистрация пользователя
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, user.getEmail());
        values.put(USER_PASSWORD, user.getPassword());
        values.put(USER_FULL_NAME, user.getFullName());
        values.put(USER_PHONE, user.getPhone());
        values.put(USER_DRIVER_LICENSE, user.getDriverLicense());
        values.put(USER_REGISTRATION_DATE, System.currentTimeMillis());
        values.put(USER_PROFILE_IMAGE, user.getProfileImageUrl());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Вход пользователя
    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                USER_EMAIL + "=? AND " + USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(USER_FULL_NAME)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE)));
            user.setDriverLicense(cursor.getString(cursor.getColumnIndexOrThrow(USER_DRIVER_LICENSE)));
            user.setRegistrationDate(cursor.getLong(cursor.getColumnIndexOrThrow(USER_REGISTRATION_DATE)));
            user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(USER_PROFILE_IMAGE)));
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Проверка существования email
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USER_ID},
                USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    // Получение пользователя по ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(USER_FULL_NAME)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(USER_PHONE)));
            user.setDriverLicense(cursor.getString(cursor.getColumnIndexOrThrow(USER_DRIVER_LICENSE)));
            user.setRegistrationDate(cursor.getLong(cursor.getColumnIndexOrThrow(USER_REGISTRATION_DATE)));
            user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(USER_PROFILE_IMAGE)));
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Обновление профиля пользователя
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_FULL_NAME, user.getFullName());
        values.put(USER_PHONE, user.getPhone());
        values.put(USER_DRIVER_LICENSE, user.getDriverLicense());
        values.put(USER_PROFILE_IMAGE, user.getProfileImageUrl());

        int result = db.update(TABLE_USERS, values, USER_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return result;
    }

    // Изменение пароля
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USER_PASSWORD},
                USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String currentPassword = cursor.getString(0);
            cursor.close();

            if (currentPassword.equals(oldPassword)) {
                ContentValues values = new ContentValues();
                values.put(USER_PASSWORD, newPassword);
                int result = db.update(TABLE_USERS, values, USER_ID + "=?",
                        new String[]{String.valueOf(userId)});
                db.close();
                return result > 0;
            }
        }
        if (cursor != null) cursor.close();
        db.close();
        return false;
    }

    // Получение аренд конкретного пользователя
    public List<Rental> getUserRentals(int userId) {
        List<Rental> rentalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS +
                " WHERE " + RENTAL_USER_ID + " = ? ORDER BY " + RENTAL_START_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Rental rental = new Rental();
                rental.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_ID)));
                rental.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_CAR_ID)));
                rental.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_USER_ID)));
                rental.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME)));
                rental.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME)));
                rental.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RENTAL_TOTAL_PRICE)));
                rental.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_STATUS)));
                rental.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_PICKUP_LOCATION)));
                rental.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_RETURN_LOCATION)));
                rentalList.add(rental);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rentalList;
    }

    // Получение активных аренд пользователя
    public List<Rental> getUserActiveRentals(int userId) {
        List<Rental> rentalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS +
                " WHERE " + RENTAL_USER_ID + " = ? AND " + RENTAL_STATUS + " = 'active'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Rental rental = new Rental();
                rental.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_ID)));
                rental.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_CAR_ID)));
                rental.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_USER_ID)));
                rental.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME)));
                rental.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME)));
                rental.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RENTAL_TOTAL_PRICE)));
                rental.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_STATUS)));
                rental.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_PICKUP_LOCATION)));
                rental.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_RETURN_LOCATION)));
                rentalList.add(rental);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rentalList;
    }


    // Проверка, арендован ли автомобиль в данный момент
    public boolean isCarRented(int carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS +
                " WHERE " + RENTAL_CAR_ID + " = ? AND " + RENTAL_STATUS + " = 'active'";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(carId)});
        boolean isRented = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return isRented;
    }

    // Получение активной аренды по ID автомобиля
    public Rental getActiveRentalByCarId(int carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RENTALS +
                " WHERE " + RENTAL_CAR_ID + " = ? AND " + RENTAL_STATUS + " = 'active'";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(carId)});

        if (cursor != null && cursor.moveToFirst()) {
            Rental rental = new Rental();
            rental.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_ID)));
            rental.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_CAR_ID)));
            rental.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_USER_ID)));
            rental.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_START_TIME)));
            rental.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(RENTAL_END_TIME)));
            rental.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(RENTAL_TOTAL_PRICE)));
            rental.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_STATUS)));
            rental.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_PICKUP_LOCATION)));
            rental.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(RENTAL_RETURN_LOCATION)));
            cursor.close();
            db.close();
            return rental;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Подсчет количества автомобилей
    public int getCarsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CARS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Подсчет доступных автомобилей
    public int getAvailableCarsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CARS +
                " WHERE " + CAR_IS_AVAILABLE + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
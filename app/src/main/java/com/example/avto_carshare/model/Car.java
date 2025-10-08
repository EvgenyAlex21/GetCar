package com.example.avto_carshare.model;

// Модель данных для автомобиля
public class Car {
    private int id;
    private String brand;
    private String model;
    private String licensePlate;
    private String carType;
    private int year;
    private String color;
    private double pricePerHour;
    private double pricePerDay;
    private boolean isAvailable;
    private String imageUrl;
    private String location;
    private double fuelLevel;
    private int seats;

    public Car() {
    }

    // Полный конструктор
    public Car(int id, String brand, String model, String licensePlate, String carType,
               int year, String color, double pricePerHour, double pricePerDay,
               boolean isAvailable, String imageUrl, String location, double fuelLevel, int seats) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.carType = carType;
        this.year = year;
        this.color = color;
        this.pricePerHour = pricePerHour;
        this.pricePerDay = pricePerDay;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.location = location;
        this.fuelLevel = fuelLevel;
        this.seats = seats;
    }

    // Метод для получения данных об автомобиле
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }
}
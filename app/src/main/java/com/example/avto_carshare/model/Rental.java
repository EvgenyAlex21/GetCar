package com.example.avto_carshare.model;

// Модель данных для аренды автомобиля
public class Rental {
    private int id;
    private int carId;
    private int userId;
    private long startTime;
    private long endTime;
    private double totalPrice;
    private String status;
    private String pickupLocation;
    private String returnLocation;

    public Rental() {
    }

    // Полный конструктор
    public Rental(int id, int carId, int userId, long startTime, long endTime,
                  double totalPrice, String status, String pickupLocation, String returnLocation) {
        this.id = id;
        this.carId = carId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
    }

    // Метод для получения данных об арендах
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
    }
}
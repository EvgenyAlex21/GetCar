package com.example.avto_carshare.model;

// Модель данных для пользователя
public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String driverLicense;
    private long registrationDate;
    private String profileImageUrl;

    public User() {
    }

    // Полный конструктор
    public User(int id, String email, String password, String fullName, String phone,
                String driverLicense, long registrationDate, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.driverLicense = driverLicense;
        this.registrationDate = registrationDate;
        this.profileImageUrl = profileImageUrl;
    }

    // Метод для получения данных о пользователях
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
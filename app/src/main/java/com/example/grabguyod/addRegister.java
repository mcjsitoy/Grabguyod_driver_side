package com.example.grabguyod;

public class addRegister {
    String Email;
    String Password;
    String FullName;
    String UserType;
    String DriverLicense;
    String PhoneNumber;
    String licensePlate;

    public addRegister(String Email, String Password, String FullName, String UserType, String DriverLicense, String PhoneNumber, String licensePlate) {
        this.Email = Email;
        this.Password = Password;
        this.FullName = FullName;
        this.UserType = UserType;
        this.DriverLicense = DriverLicense;
        this.PhoneNumber = PhoneNumber;
        this.licensePlate = licensePlate;
    }

    public addRegister(String Email, String Password, String FullName, String UserType) {
        this.Email = Email;
        this.Password = Password;
        this.FullName = FullName;
        this.UserType = UserType;
    }

    public String getTb_Email() {
        return Email;
    }

    public String getTb_Password() {
        return Password;
    }

    public String getTb_FullName() {
        return FullName;
    }

    public String getTb_UserType() {
        return UserType;
    }

    public String getTb_DriverLicense() {
        return DriverLicense;
    }

    public String getTb_PhoneNumber() {
        return PhoneNumber;
    }

    public String getLicensePlate() {
            return licensePlate;
    }
}

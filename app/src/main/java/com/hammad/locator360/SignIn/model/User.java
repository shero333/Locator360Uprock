package com.hammad.locator360.SignIn.model;

public class User {

    private String firstName;
    private String phoneNo;
    private String email;
    private String password;

    public User(String firstName, String phoneNo, String email, String password) {
        this.firstName = firstName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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
}

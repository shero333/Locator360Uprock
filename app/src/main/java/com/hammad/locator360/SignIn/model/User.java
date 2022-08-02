package com.hammad.locator360.SignIn.model;

public class User {

    private String firstName;
    private String phoneNo;
    private String email;


    public User(String firstName, String phoneNo, String email) {
        this.firstName = firstName;
        this.phoneNo = phoneNo;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

}

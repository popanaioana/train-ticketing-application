package com.siemens.model;

public class Passenger {
    private String fullName;
    private String email;

    public Passenger() {

    }

    public Passenger(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return fullName + " <" + email + ">";
    }
}
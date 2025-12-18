package com.example.dineo.models;

/**
 * User Model - Managed via API (not stored locally)
 * Student ID: BSSE2506008
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String contact;
    private String usertype; // "GUEST" or "STAFF"

    // Constructors
    public User() {}

    public User(String username, String password, String firstname, String lastname,
                String email, String contact, String usertype) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
        this.usertype = usertype;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getUsertype() {
        return usertype;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    // Helper methods
    public String getFullName() {
        return firstname + " " + lastname;
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(usertype);
    }

    public boolean isGuest() {
        return "GUEST".equalsIgnoreCase(usertype);
    }
}
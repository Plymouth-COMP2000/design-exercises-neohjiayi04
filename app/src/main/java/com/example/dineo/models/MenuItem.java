package com.example.dineo.models;

/**
 * MenuItem Model - Stored locally in SQLite
 * Student ID: BSSE2506008
 */
public class MenuItem {
    private int id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;

    // Constructors
    public MenuItem() {}

    public MenuItem(String name, double price, String imageUrl, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper method
    public String getPriceFormatted() {
        return String.format("RM %.2f", price);
    }
}
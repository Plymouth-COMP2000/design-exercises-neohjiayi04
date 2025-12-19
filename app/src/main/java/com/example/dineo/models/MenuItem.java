package com.example.dineo.models;

/**
 * MenuItem Model
 * Student ID: BSSE2506008
 */
public class MenuItem {

    private int id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;
    private String category;

    public MenuItem() {
    }

    public MenuItem(int id, String name, double price, String imageUrl, String description, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
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

    public String getCategory() {
        return category;
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

    public void setCategory(String category) {
        this.category = category;
    }

    // Helper method for formatted price
    public String getPriceFormatted() {
        return String.format("RM %.2f", price);
    }
}
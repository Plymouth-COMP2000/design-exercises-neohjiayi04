package com.example.dineo;

public class GuestFoodItem {
    private String name;
    private double price;
    private String category;
    private int imageResource;
    private String description;

    public GuestFoodItem(String name, double price, String category, int imageResource, String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageResource = imageResource;
        this.description = description;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
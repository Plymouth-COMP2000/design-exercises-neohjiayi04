package com.example.dineo.models;

/**
 * MenuItem model - null-safe getters
 */
public class MenuItem {

    private int id = -1;
    private String name = "";
    private double price = 0.0;
    private String description = "";
    private String category = "";
    private String imageUrl = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price >= 0 ? price : 0.0;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Optional: formatted price for display
    public String getPriceFormatted() {
        return String.format("RM %.2f", getPrice());
    }
}

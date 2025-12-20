package com.example.dineo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * MenuItem model - Parcelable + null-safe getters
 */
public class MenuItem implements Parcelable {

    private int id = -1;
    private String name = "";
    private double price = 0.0;
    private String description = "";
    private String category = "";
    private String imageUrl = "";

    public MenuItem() {}

    protected MenuItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
        description = in.readString();
        category = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(imageUrl);
    }

    // ===== Existing getters & setters (unchanged) =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price >= 0 ? price : 0.0; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category != null ? category : ""; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl != null ? imageUrl : ""; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPriceFormatted() {
        return String.format("RM %.2f", getPrice());
    }
}

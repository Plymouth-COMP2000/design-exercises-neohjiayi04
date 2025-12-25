package com.example.dineo.models;

<<<<<<< HEAD
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * MenuItem model - UPDATED to support byte[] images
 * Backward compatible with String imageUrl
 * Student ID: BSSE2506008
 */
public class MenuItem implements Parcelable {

    private int id = -1;
    private String name = "";
    private double price = 0.0;
    private String description = "";
    private String category = "";
    private String imageUrl = "";

    // ==================== CONSTRUCTORS ====================

    public MenuItem() {}

    public MenuItem(int id, String name, String description, double price, String category, byte[] imageBytes) {
=======
public class MenuItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imagePath;

    // Constructor without ID (for new items)
    public MenuItem(String name, String description, double price,
                    String category, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Constructor with ID (for existing items)
    public MenuItem(int id, String name, String description, double price,
                    String category, String imagePath) {
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
<<<<<<< HEAD
        setImage(imageBytes); // Convert bytes to Base64 string
    }

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

    // ==================== IMAGE METHODS (NEW) ====================

    /**
     * Set image from byte array
     * Converts to Base64 string for storage
     */
    public void setImage(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            this.imageUrl = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } else {
            this.imageUrl = "";
        }
    }

    /**
     * Get image as byte array
     * Converts from Base64 string
     */
    public byte[] getImage() {
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
            try {
                return Base64.decode(imageUrl, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Check if has valid image data
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isEmpty();
    }

    // ==================== EXISTING GETTERS & SETTERS ====================

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
=======
        this.imagePath = imagePath;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
}
package com.example.grabngo.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "RESTAURANT",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "manager_id"
                )
        },
        indices = {
                // Index improves search performance when querying restaurants by Manager ID.
                @Index(value = {"manager_id"}, unique = true)
        }
)
public class Restaurant {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    @ColumnInfo(name = "manager_id")
    private int managerId;

    @NonNull
    @ColumnInfo(name = "restaurant_name")
    private String restaurantName;

    @NonNull
    @ColumnInfo(name = "address")
    private String address;

    @NonNull
    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "contact_info")
    private String contactInfo;

    @ColumnInfo(name = "opening_hours")
    private String openingHours;

    @ColumnInfo(name = "rating")
    private Double rating;

    @ColumnInfo(name = "restaurant_image")
    private String restaurantImage;

    // Default Constructor
    public Restaurant() {
    }

    // Constructor
    public Restaurant(int managerId,
                      @NonNull String restaurantName,
                      @NonNull String address,
                      @NonNull String city,
                      String contactInfo,
                      String openingHours,
                      Double rating,
                      String restaurantImage) {

        this.managerId = managerId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.city = city;
        this.contactInfo = contactInfo;
        this.openingHours = openingHours;
        this.rating = rating;
        this.restaurantImage = restaurantImage;
    }

    // Getters
    public int getRestaurantId() { return restaurantId; }
    public int getManagerId() { return managerId; }
    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getContactInfo() { return contactInfo; }
    public String getOpeningHours() { return openingHours; }
    public Double getRating() { return rating; }
    public String getRestaurantImage() { return restaurantImage; }

    // Setters
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }
    public void setRestaurantName(@NonNull String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public void setAddress(@NonNull String address) { this.address = address; }
    public void setCity(@NonNull String city) { this.city = city; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }
}

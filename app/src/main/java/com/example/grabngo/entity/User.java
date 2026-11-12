package com.example.grabngo.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "USER",
        indices = {
                @Index(value = {"email"}, unique = true)
        }
)
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    @NonNull
    @ColumnInfo(name = "address")
    private String address;

    @NonNull
    @ColumnInfo(name = "role")
    private String role;

    @ColumnInfo(name = "profile_photo")
    private String profilePhoto;

    //Default Constructor
    public User() {
    }
    // Constructor
    public User(@NonNull String name,
                @NonNull String email,
                @NonNull String password,
                @NonNull String address,
                @NonNull String role,
                String profilePhoto) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.profilePhoto = profilePhoto;
    }

    //Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public String getProfilePhoto() { return profilePhoto; }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}



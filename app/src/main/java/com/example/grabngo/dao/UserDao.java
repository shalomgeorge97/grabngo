
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.User;


@Dao
public interface UserDao {

    // Insert a new user for registration
    @Insert
    long addUser(User user);

    // Get a user by ID for profile screen etc.
    @Query("SELECT * FROM USER WHERE user_id = :userId")
    User getUserById(int userId);

    // Get a user by email for login etc.
    @Query("SELECT * FROM USER WHERE email = :email")
    User getUserByEmail(String email);

    // Check whether email already exists during registration
    @Query("SELECT EXISTS(SELECT 1 FROM USER WHERE email = :email)")
    boolean isEmailExists(String email);

    // Update an existing user for profile edit
    @Update
    void update(User user);

}


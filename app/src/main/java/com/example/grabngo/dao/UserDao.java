//implemented by Ami
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.User;


@Dao
public interface UserDao {

    @Insert
    long addUser(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM USER WHERE user_id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM USER WHERE email = :email")
    User getUserByEmail(String email);

    //For checking if the email address already exists during registration
    @Query("SELECT EXISTS(SELECT 1 FROM USER WHERE email = :email)")
    boolean isEmailExists(String email);

}


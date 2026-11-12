package com.example.grabngo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.grabngo.dao.FoodItemDao;
import com.example.grabngo.dao.OrderDao;
import com.example.grabngo.dao.OrderItemDao;
import com.example.grabngo.dao.ReminderDao;
import com.example.grabngo.dao.RestaurantDao;
import com.example.grabngo.dao.UserDao;
import com.example.grabngo.entity.FoodItem;
import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.OrderItem;
import com.example.grabngo.entity.Reminder;
import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.entity.User;


@Database(
        entities = {
                User.class,
                Restaurant.class,
                FoodItem.class,
                Order.class,
                OrderItem.class,
                Reminder.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {

    // Store the Room database instance so the same instance can be reused across the app.
    private static volatile AppDatabase appDatabase;

    // DAO accessors
    public abstract UserDao userDao();

    public abstract RestaurantDao restaurantDao();

    public abstract FoodItemDao foodItemDao();

    public abstract OrderDao orderDao();

    public abstract OrderItemDao orderItemDao();

    public abstract ReminderDao reminderDao();


    //Get database instance
    //Use Singleton to reuse one database instance across the app,
    //reducing resource usage and simplifying database management.
    public static AppDatabase getDatabase(Context context) {

        //Check whether a database instance has already been created.
        if (appDatabase == null) {
            // Allow only one thread at a time to create the database instance.
            synchronized (AppDatabase.class) {

                //Check again in case another thread created the instance
                //while this thread was waiting to enter the synchronized block.
                if (appDatabase == null) {

                    //Create a Room database instance and store it in appDatabase.
                    appDatabase = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "grabngo_database"
                    ).build();
                }
            }
        }

        return appDatabase;
    }

}

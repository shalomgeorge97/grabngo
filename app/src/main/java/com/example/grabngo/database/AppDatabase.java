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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;


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
                    ).addCallback(new RoomDatabase.Callback() {

                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // Create Manager account
                                    ContentValues managerValues = new ContentValues();
                                    managerValues.put("name", "Test Manager");
                                    managerValues.put("email", "manager@grabngo.com");
                                    managerValues.put("password", "manager123");
                                    managerValues.put("address", "Vancouver, BC");
                                    managerValues.put("role", "MANAGER");

                                    long managerId = db.insert(
                                            "USER",
                                            SQLiteDatabase.CONFLICT_ABORT,
                                            managerValues
                                    );

                                    // Create Restaurant linked to Manager
                                    ContentValues restaurantValues = new ContentValues();
                                    restaurantValues.put("manager_id", managerId);
                                    restaurantValues.put("restaurant_name", "GrabNGo Restaurant");
                                    restaurantValues.put("address", "123 Main Street");
                                    restaurantValues.put("city", "Vancouver");
                                    restaurantValues.put("contact_info", "604-000-0000");
                                    restaurantValues.put("opening_hours", "09:00-18:00");
                                    restaurantValues.put("rating", 4.5);

                                    db.insert(
                                            "RESTAURANT",
                                            SQLiteDatabase.CONFLICT_ABORT,
                                            restaurantValues
                                    );
                                }

                            })
                            .build();
                }
            }
        }

        return appDatabase;
    }

}

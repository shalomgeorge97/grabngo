package com.example.grabngo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.grabngo.dao.FoodItemDao;
import com.example.grabngo.dao.OrderDao;
import com.example.grabngo.dao.OrderItemDao;
import com.example.grabngo.dao.OrderTransactionDao;
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

    public abstract OrderTransactionDao orderTransactionDao();


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

    public void insertSeedData() {

        if (userDao().isEmailExists("manager@grabngo.com")) {
            return;
        }

        // 1. Create Manager account
        User manager = new User(
                "Test Manager",
                "manager@grabngo.com",
                "manager123",
                "Vancouver, BC",
                "MANAGER",
                null
        );

        long managerId = userDao().addUser(manager);


        // 2. Create Customer account
        User customer = new User(
                "Test Customer",
                "customer@grabngo.com",
                "customer123",
                "Vancouver, BC",
                "CUSTOMER",
                null
        );

        userDao().addUser(customer);


        // 3. Create Restaurants

        Restaurant restaurant1 = new Restaurant(
                (int) managerId,
                "Vancouver Sushi",
                "100 Robson Street",
                "Vancouver",
                "604-111-1111",
                "09:00-20:00",
                4.5,
                null
        );

        long restaurantId1 = restaurantDao().addRestaurant(restaurant1);


        Restaurant restaurant2 = new Restaurant(
                (int) managerId,
                "Fresh Bowl Cafe",
                "200 Granville Street",
                "Vancouver",
                "604-222-2222",
                "08:00-18:00",
                4.3,
                null
        );

        long restaurantId2 = restaurantDao().addRestaurant(restaurant2);


        Restaurant restaurant3 = new Restaurant(
                (int) managerId,
                "Downtown Bakery",
                "300 Main Street",
                "Vancouver",
                "604-333-3333",
                "07:00-17:00",
                4.7,
                null
        );

        long restaurantId3 = restaurantDao().addRestaurant(restaurant3);


        // 4. Create Food Items

        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId1,
                "Salmon Sushi Box",
                "Fresh salmon sushi set",
                "Japanese",
                "Salmon, Rice",
                15.00,
                8.00,
                5,
                "2026-12-01 18:00"
        ));

        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId1,
                "California Roll",
                "California roll with avocado",
                "Japanese",
                "Crab, Avocado, Rice",
                12.00,
                6.00,
                4,
                "2026-12-01 18:00"
        ));


        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId2,
                "Chicken Rice Bowl",
                "Rice bowl with grilled chicken",
                "Rice Bowl",
                "Chicken, Rice, Vegetables",
                16.00,
                9.00,
                3,
                "2026-12-01 17:00"
        ));

        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId2,
                "Vegetable Salad",
                "Fresh mixed vegetable salad",
                "Salad",
                "Lettuce, Tomato, Cucumber",
                12.00,
                7.00,
                5,
                "2026-12-01 17:00"
        ));


        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId3,
                "Croissant Set",
                "Freshly baked croissants",
                "Bakery",
                "Wheat, Butter, Milk",
                10.00,
                5.00,
                6,
                "2026-12-01 16:00"
        ));

        foodItemDao().addFoodItem(new FoodItem(
                (int) restaurantId3,
                "Muffins (2 pcs)",
                "Two freshly baked muffins",
                "Bakery",
                "Wheat, Eggs, Milk",
                8.00,
                4.00,
                4,
                "2026-12-01 16:00"
        ));
    }

}



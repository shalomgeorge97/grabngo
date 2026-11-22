package com.example.grabngo.database;

import android.content.Context;
import android.content.SharedPreferences;

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
import com.example.grabngo.util.OrderStatus;


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

    private static AppDatabase appDatabase;

    // DAO accessors
    public abstract UserDao userDao();

    public abstract RestaurantDao restaurantDao();

    public abstract FoodItemDao foodItemDao();

    public abstract OrderDao orderDao();

    public abstract OrderItemDao orderItemDao();

    public abstract ReminderDao reminderDao();

    public abstract OrderTransactionDao orderTransactionDao();



    public static AppDatabase getDatabase(Context context) {

        if (appDatabase == null) {

            appDatabase = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "grabngo_database"
            ).allowMainThreadQueries().build();
        }

        return appDatabase;
    }

    public void insertSeedData(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences("GrabNGoDatabase", Context.MODE_PRIVATE);

        boolean isSeeded = preferences.getBoolean("is_seeded", false);

        if (isSeeded) {
            return;
        }




            User manager1 = new User(
                    "Test Manager 1",
                    "manager1@grabngo.com",
                    "manager123",
                    "Vancouver, BC",
                    "MANAGER",
                    null
            );
            long managerId1 = userDao().addUser(manager1);


            User manager2 = new User(
                    "Test Manager 2",
                    "manager2@grabngo.com",
                    "manager123",
                    "Vancouver, BC",
                    "MANAGER",
                    null
            );
            long managerId2 = userDao().addUser(manager2);


            User manager3 = new User(
                    "Test Manager 3",
                    "manager3@grabngo.com",
                    "manager123",
                    "Vancouver, BC",
                    "MANAGER",
                    null
            );
            long managerId3 = userDao().addUser(manager3);


            User customer = new User(
                    "Test Customer",
                    "customer@grabngo.com",
                    "customer123",
                    "Vancouver, BC",
                    "CUSTOMER",
                    null
            );
            userDao().addUser(customer);


            Restaurant restaurant1 = new Restaurant(
                    (int) managerId1,
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
                    (int) managerId2,
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
                    (int) managerId3,
                    "Downtown Bakery",
                    "300 Main Street",
                    "Vancouver",
                    "604-333-3333",
                    "07:00-17:00",
                    4.7,
                    null
            );
            long restaurantId3 = restaurantDao().addRestaurant(restaurant3);


            foodItemDao().addFoodItem(new FoodItem(
                    (int) restaurantId1,
                    "Salmon Sushi Box",
                    "Fresh salmon sushi set",
                    "Japanese",
                    "Salmon, Rice",
                    15.00,
                    8.00,
                    5,
                    "2026-12-10 18:00"
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
                    "2026-12-10 18:00"
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
                    "2026-12-10 17:00"
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
                    "2026-12-10 17:00"
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
                    "2026-12-10 16:00"
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
                    "2026-12-10 16:00"
            ));


        orderDao().addOrder(new Order(
                4,
                1,
                "2026-11-22 10:06",
                OrderStatus.FULFILLMENT_PICKUP,
                OrderStatus.PENDING,
                16.00,
                null
        ));

        orderDao().addOrder(new Order(
                4,
                1,
                "2026-11-22 10:06",
                OrderStatus.FULFILLMENT_DELIVERY,
                OrderStatus.PENDING,
                16.00,
                null
        ));

        preferences.edit()
                .putBoolean("is_seeded", true)
                .apply();
    }



}



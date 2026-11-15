
//only for testing -> jenu
package com.example.grabngo;

import com.example.grabngo.entity.FoodItem;
import com.example.grabngo.entity.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TEMPORARY test data. Delete this class when the screens read from the database.
public class DummyData {

    private DummyData() { }

    public static Restaurant getRestaurantById(int id) {
        Restaurant r;
        switch (id) {
            case 1:
                r = new Restaurant(1, "Green Bowl", "123 Main St", "New Westminster",
                        "604-555-0101", "9am - 9pm", 4.5, null);
                break;
            case 2:
                r = new Restaurant(2, "Fresh Bakery", "45 Royal Ave", "New Westminster",
                        "604-555-0102", "7am - 6pm", 4.2, null);
                break;
            case 3:
                r = new Restaurant(3, "Spice Kitchen", "88 Columbia St", "Vancouver",
                        "604-555-0103", "11am - 10pm", 4.7, null);
                break;
            default:
                return null;
        }
        r.setRestaurantId(id);
        return r;
    }

    // Returns the restaurant's food items, filtered by name when a keyword is given
    public static List<FoodItem> getFoodItems(int restaurantId, String keyword) {
        List<FoodItem> all = new ArrayList<>();

        if (restaurantId == 1) {
            all.add(food(1, 1, "Chicken Rice Bowl", "Rice bowl with grilled chicken",
                    "Rice Bowl", "Chicken, Rice, Vegetables", 16.00, 9.00, 3));
            all.add(food(2, 1, "Vegetable Salad", "Fresh mixed vegetable salad",
                    "Salad", "Lettuce, Tomato, Cucumber", 12.00, 7.00, 5));
            all.add(food(3, 1, "Salmon Poke Bowl", "Salmon with rice and avocado",
                    "Rice Bowl", "Salmon, Rice, Avocado", 15.00, 8.00, 4));
            all.add(food(4, 1, "Tofu Wrap", "Tofu wrap with greens",
                    "Wrap", "Wheat, Tofu, Soy", 11.00, 6.00, 2));
            all.add(food(5, 1, "Fruit Cup", "Seasonal fruit cup",
                    "Dessert", "Mixed fruit", 7.00, 3.50, 6));
        } else if (restaurantId == 2) {
            all.add(food(6, 2, "Croissant Set", "Freshly baked croissants",
                    "Bakery", "Wheat, Butter, Milk", 10.00, 5.00, 6));
            all.add(food(7, 2, "Muffins (2 pcs)", "Two freshly baked muffins",
                    "Bakery", "Wheat, Eggs, Milk", 8.00, 4.00, 4));
            all.add(food(8, 2, "Sourdough Loaf", "Day-old sourdough loaf",
                    "Bakery", "Wheat", 9.00, 5.00, 2));
        } else if (restaurantId == 3) {
            all.add(food(9, 3, "Butter Chicken Combo", "Butter chicken with rice and naan",
                    "Combo", "Chicken, Milk, Wheat", 18.00, 10.00, 1));
        }

        if (keyword == null || keyword.isEmpty()) {
            return all;
        }

        List<FoodItem> filtered = new ArrayList<>();
        String search = keyword.toLowerCase(Locale.ROOT);
        for (FoodItem item : all) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(search)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private static FoodItem food(int foodId, int restaurantId, String name,
                                 String description, String category, String ingredients,
                                 double originalPrice, double discountedPrice, int quantity) {
        FoodItem item = new FoodItem(restaurantId, name, description, category, ingredients,
                originalPrice, discountedPrice, quantity, "2026-12-01 18:00");
        item.setFoodId(foodId);
        return item;
    }
}
package com.example.grabngo.util;

import android.app.Activity;
import android.content.Intent;

import com.example.grabngo.FoodList;
import com.example.grabngo.Home;
import com.example.grabngo.OrderHistory;
import com.example.grabngo.OrderList;
import com.example.grabngo.Profile;
import com.example.grabngo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class BottomNavigationHelper {

    private BottomNavigationHelper() {
        // Prevent creating instances of this utility class.
    }

    public static void configure(
            Activity activity,
            BottomNavigationView bottomNavigationView,
            int selectedItemId) {

        if (bottomNavigationView == null) {
            throw new IllegalStateException(
                    "BottomNavigationView is missing from the layout.");
        }

        SessionManager sessionManager =
                new SessionManager(activity);

        boolean isManager =
                SessionManager.ROLE_MANAGER.equals(
                        sessionManager.getUserRole());

        // Load the correct menu for the user's role.
        bottomNavigationView.getMenu().clear();

        if (isManager) {
            bottomNavigationView.inflateMenu(
                    R.menu.menu_manager_bottom_nav);
        } else {
            bottomNavigationView.inflateMenu(
                    R.menu.menu_customer_bottom_nav);
        }

        bottomNavigationView.setSelectedItemId(selectedItemId);

        // Handle navigation selections.
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Class<?> destination = getDestination(
                    isManager, item.getItemId());

            if (destination == null) {
                return false;
            }

            // Stay on the current screen if already selected.
            if (destination.equals(activity.getClass())) {
                return true;
            }

            Intent intent = new Intent(activity, destination);

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);

            return true;
        });
    }

    private static Class<?> getDestination(
            boolean isManager, int itemId) {

        if (itemId == R.id.nav_profile) {
            return Profile.class;
        }

        if (isManager) {

            if (itemId == R.id.nav_food) {
                return FoodList.class;
            }

            if (itemId == R.id.nav_orders) {
                return OrderList.class;
            }

        } else {

            if (itemId == R.id.nav_home) {
                return Home.class;
            }

            if (itemId == R.id.nav_orders) {
                return OrderHistory.class;
            }
        }

        return null;
    }
}

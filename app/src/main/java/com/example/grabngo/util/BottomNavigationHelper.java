package com.example.grabngo.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grabngo.FoodList;
import com.example.grabngo.Home;
import com.example.grabngo.OrderHistory;
import com.example.grabngo.OrderList;
import com.example.grabngo.Profile;
import com.example.grabngo.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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

        // Load the correct navigation menu.
        bottomNavigationView.getMenu().clear();

        if (isManager) {
            bottomNavigationView.inflateMenu(
                    R.menu.menu_manager_bottom_nav);
        } else {
            bottomNavigationView.inflateMenu(
                    R.menu.menu_customer_bottom_nav);
        }

        // Apply consistent appearance to every screen.
        applyNavigationStyle(activity, bottomNavigationView);

        // Select the current screen.
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

    private static void applyNavigationStyle(
            Activity activity,
            BottomNavigationView bottomNavigationView) {


        int lightGreen = ContextCompat.getColor(
                activity, R.color.primary_container);

        int white = ContextCompat.getColor(
                activity, R.color.surface);

        // Apply selected and unselected colors.
        ColorStateList itemColors =
                ContextCompat.getColorStateList(
                        activity, R.color.bottom_nav_tint);

        bottomNavigationView.setItemIconTintList(itemColors);
        bottomNavigationView.setItemTextColor(itemColors);

        // Keep labels visible on all screens.
        bottomNavigationView.setLabelVisibilityMode(
                NavigationBarView.LABEL_VISIBILITY_LABELED);

        // Standardize icon dimensions.
        bottomNavigationView.setItemIconSize(
                dp(activity, 24));

        // Standardize vertical spacing.
        bottomNavigationView.setItemPaddingTop(
                dp(activity, 6));

        bottomNavigationView.setItemPaddingBottom(
                dp(activity, 6));

        // Highlight the selected navigation item.
        bottomNavigationView.setItemActiveIndicatorEnabled(true);

        bottomNavigationView.setItemActiveIndicatorColor(
                ColorStateList.valueOf(lightGreen));

        bottomNavigationView.setItemActiveIndicatorWidth(
                dp(activity, 64));

        bottomNavigationView.setItemActiveIndicatorHeight(
                dp(activity, 32));

        // Subtle selection ripple.
        bottomNavigationView.setItemRippleColor(
                ColorStateList.valueOf(lightGreen));

        // Consistent background and elevation.
        bottomNavigationView.setBackgroundColor(white);
        bottomNavigationView.setElevation(dp(activity, 8));

        // Prevent labels and icons from shifting horizontally.
        bottomNavigationView.setItemHorizontalTranslationEnabled(false);

        // Apply the Android system navigation-bar inset
        // inside the bottom navigation instead of the entire root.
        final int initialLeft = bottomNavigationView.getPaddingLeft();
        final int initialTop = bottomNavigationView.getPaddingTop();
        final int initialRight = bottomNavigationView.getPaddingRight();
        final int initialBottom = bottomNavigationView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                bottomNavigationView,
                (view, insets) -> {

                    int bottomInset = insets.getInsets(
                            WindowInsetsCompat.Type.navigationBars()
                    ).bottom;

                    view.setPadding(
                            initialLeft,
                            initialTop,
                            initialRight,
                            initialBottom + bottomInset
                    );

                    return insets;
                });

        ViewCompat.requestApplyInsets(bottomNavigationView);
    }

    private static int dp(Activity activity, int value) {
        return Math.round(
                value * activity.getResources()
                        .getDisplayMetrics().density);
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

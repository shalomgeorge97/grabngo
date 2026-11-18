
package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.util.BottomNavigationHelper;
import com.example.grabngo.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FoodList extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        // Only allow authenticated managers to access this screen.
        if (!sessionManager.hasActiveSession()
                || !SessionManager.ROLE_MANAGER.equals(
                sessionManager.getUserRole())) {

            openWelcome();
            return;
        }

        setContentView(R.layout.activity_food_list);

        // Configure manager-specific bottom navigation.
        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        BottomNavigationHelper.configure(
                this,
                bottomNav,
                R.id.nav_food
        );
    }

    private void openWelcome() {

        Intent intent = new Intent(this, Welcome.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}

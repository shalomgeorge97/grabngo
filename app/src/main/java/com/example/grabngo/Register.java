
package com.example.grabngo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.entity.User;
import com.example.grabngo.util.PasswordHasher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Register extends Activity {

    private static final String TAG = "GrabNGoRegister";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private Button btnCustomerRole;
    private Button btnManagerRole;
    private Button btnRegister;

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtAddress;

    private LinearLayout layoutManagerFields;
    private EditText edtRestaurantName;
    private EditText edtRestaurantAddress;
    private EditText edtRestaurantCity;

    private boolean customerSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCustomerRole = findViewById(R.id.btnRegisterCustomer);
        btnManagerRole = findViewById(R.id.btnRegisterManager);

        edtName = findViewById(R.id.edtRegisterName);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtAddress = findViewById(R.id.edtRegisterAddress);

        layoutManagerFields = findViewById(R.id.layoutManagerFields);
        edtRestaurantName =
                findViewById(R.id.edtRegisterRestaurantName);
        edtRestaurantAddress =
                findViewById(R.id.edtRegisterRestaurantAddress);
        edtRestaurantCity =
                findViewById(R.id.edtRegisterRestaurantCity);


        btnRegister = findViewById(R.id.btnRegister);

        TextView txtLoginLink =
                findViewById(R.id.txtRegisterLoginLink);

        updateRoleSelection(true);

        btnCustomerRole.setOnClickListener(v ->
                updateRoleSelection(true));

        btnManagerRole.setOnClickListener(v ->
                updateRoleSelection(false));

        btnRegister.setOnClickListener(v ->
                validateRegistration());

        txtLoginLink.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class)));
    }

    private void updateRoleSelection(boolean isCustomer) {

        customerSelected = isCustomer;

        btnCustomerRole.setBackgroundResource(isCustomer
                ? R.drawable.bg_role_selected
                : R.drawable.bg_role_unselected);

        btnManagerRole.setBackgroundResource(isCustomer
                ? R.drawable.bg_role_unselected
                : R.drawable.bg_role_selected);

        btnCustomerRole.setTextColor(getColor(isCustomer
                ? R.color.grabngo_green_dark
                : R.color.grabngo_muted));

        btnManagerRole.setTextColor(getColor(isCustomer
                ? R.color.grabngo_muted
                : R.color.grabngo_green_dark));

        layoutManagerFields.setVisibility(
                isCustomer ? View.GONE : View.VISIBLE);
    }

    private void validateRegistration() {

        String name = edtName.getText().toString().trim();

        String email = edtEmail.getText().toString()
                .trim().toLowerCase(java.util.Locale.ROOT);

        String password = edtPassword.getText().toString();

        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Please enter your name.");
            edtName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Please enter your email.");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email address.");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Please enter a password.");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            edtPassword.setError(
                    "Password must contain at least 8 characters.");
            edtPassword.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Please enter your address.");
            edtAddress.requestFocus();
            return;
        }

        String restaurantName = "";
        String restaurantAddress = "";
        String restaurantCity = "";

        if (!customerSelected) {

            restaurantName =
                    edtRestaurantName.getText().toString().trim();

            restaurantAddress =
                    edtRestaurantAddress.getText().toString().trim();

            restaurantCity =
                    edtRestaurantCity.getText().toString().trim();

            if (restaurantName.isEmpty()) {
                edtRestaurantName.setError(
                        "Please enter your restaurant name.");
                edtRestaurantName.requestFocus();
                return;
            }

            if (restaurantAddress.isEmpty()) {
                edtRestaurantAddress.setError(
                        "Please enter your restaurant address.");
                edtRestaurantAddress.requestFocus();
                return;
            }

            if (restaurantCity.isEmpty()) {
                edtRestaurantCity.setError(
                        "Please enter your restaurant city.");
                edtRestaurantCity.requestFocus();
                return;
            }
        }

        // Save the validated registration.
        saveRegistration(
                name,
                email,
                password,
                address,
                customerSelected,
                restaurantName,
                restaurantAddress,
                restaurantCity
        );
    }

    private void saveRegistration(
            String name,
            String email,
            String password,
            String address,
            boolean isCustomer,
            String restaurantName,
            String restaurantAddress,
            String restaurantCity
    ) {

        btnRegister.setEnabled(false);

        databaseExecutor.execute(() -> {

            long createdUserId = -1;
            Exception databaseError = null;

            try {

                AppDatabase database =
                        AppDatabase.getDatabase(getApplicationContext());

                final String hashedPassword =
                        PasswordHasher.hash(password);

                final String role = isCustomer
                        ? "CUSTOMER" : "MANAGER";

                final String finalRestaurantName = restaurantName;
                final String finalRestaurantAddress = restaurantAddress;
                final String finalRestaurantCity = restaurantCity;

                // Save the user and restaurant in one transaction.
                createdUserId = database.runInTransaction(() -> {

                    // Check whether the email already exists.
                    if (database.userDao().isEmailExists(email)) {
                        return -1L;
                    }

                    User newUser = new User(
                            name,
                            email,
                            hashedPassword,
                            address,
                            role,
                            null
                    );

                    long newUserId =
                            database.userDao().addUser(newUser);

                    if (newUserId <= 0) {
                        throw new IllegalStateException(
                                "Unable to create user account.");
                    }

                    // Create the restaurant for a manager.
                    if (!isCustomer) {

                        Restaurant newRestaurant = new Restaurant(
                                (int) newUserId,
                                finalRestaurantName,
                                finalRestaurantAddress,
                                finalRestaurantCity,
                                null,
                                null,
                                null,
                                null
                        );

                        database.restaurantDao()
                                .addRestaurant(newRestaurant);
                    }

                    return newUserId;
                });

            } catch (Exception exception) {

                databaseError = exception;

                Log.e(TAG,
                        "Registration database error",
                        exception);
            }

            final long finalUserId = createdUserId;
            final Exception finalError = databaseError;

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                btnRegister.setEnabled(true);

                if (finalError != null) {

                    Toast.makeText(this,
                            "Registration failed. Please try again.",
                            Toast.LENGTH_LONG).show();

                    return;
                }

                if (finalUserId == -1) {

                    edtEmail.setError(
                            "This email is already registered.");

                    edtEmail.requestFocus();
                    return;
                }

                Toast.makeText(this,
                        "Registration successful! Please log in.",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(
                        Register.this, Login.class);

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}

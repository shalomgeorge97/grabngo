package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.User;
import com.example.grabngo.util.BottomNavigationHelper;
import com.example.grabngo.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {

    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private User currentUser;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtAddress;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.hasActiveSession()) {
            openWelcomeAndClearBackStack();
            return;
        }

        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtProfileName);
        edtEmail = findViewById(R.id.edtProfileEmail);
        edtAddress = findViewById(R.id.edtProfileAddress);
        btnSave = findViewById(R.id.btnProfileSave);
        Button btnLogout = findViewById(R.id.btnProfileLogout);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        BottomNavigationHelper.configure(this, bottomNav, R.id.nav_profile);
        btnSave.setOnClickListener(v -> saveProfileChanges());
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        loadCurrentUser();
    }

    private void loadCurrentUser() {
        int userId = sessionManager.getUserId();
        btnSave.setEnabled(false);

        databaseExecutor.execute(() -> {
            User user = null;
            Exception loadError = null;
            try {
                user = AppDatabase.getDatabase(getApplicationContext())
                        .userDao().getUserById(userId);
            } catch (Exception exception) {
                loadError = exception;
            }

            final User loadedUser = user;
            final Exception error = loadError;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (error != null) {
                    Toast.makeText(this, R.string.error_profile_load,
                            Toast.LENGTH_LONG).show();
                    btnSave.setEnabled(true);
                    return;
                }

                if (loadedUser == null) {
                    sessionManager.clearSession();
                    Toast.makeText(this, R.string.error_profile_user_missing,
                            Toast.LENGTH_LONG).show();
                    openWelcomeAndClearBackStack();
                    return;
                }

                currentUser = loadedUser;
                edtName.setText(loadedUser.getName());
                edtEmail.setText(loadedUser.getEmail());
                edtAddress.setText(loadedUser.getAddress());
                btnSave.setEnabled(true);
            });
        });
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(this, R.string.error_profile_not_loaded,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError(getString(R.string.error_name_required));
            edtName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            edtEmail.setError(getString(R.string.error_email_required));
            edtEmail.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            edtAddress.setError(getString(R.string.error_address_required));
            edtAddress.requestFocus();
            return;
        }

        btnSave.setEnabled(false);
        int currentUserId = currentUser.getUserId();
        databaseExecutor.execute(() -> {
            User updatedUser = null;
            boolean emailInUse = false;
            Exception saveError = null;

            try {
                AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
                emailInUse = database.userDao().isEmailUsedByAnotherUser(email, currentUserId);

                if (!emailInUse) {
                    // Reload the row on the worker thread, then update the database record.
                    updatedUser = database.userDao().getUserById(currentUserId);
                    if (updatedUser != null) {
                        updatedUser.setName(name);
                        updatedUser.setEmail(email);
                        updatedUser.setAddress(address);
                        database.userDao().update(updatedUser);
                    }
                }
            } catch (Exception exception) {
                saveError = exception;
            }

            final User savedUser = updatedUser;
            final boolean duplicateEmail = emailInUse;
            final Exception error = saveError;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                btnSave.setEnabled(true);

                if (error != null) {
                    Toast.makeText(this, R.string.error_profile_save,
                            Toast.LENGTH_LONG).show();
                } else if (duplicateEmail) {
                    edtEmail.setError(getString(R.string.error_email_in_use));
                    edtEmail.requestFocus();
                } else if (savedUser == null) {
                    sessionManager.clearSession();
                    openWelcomeAndClearBackStack();
                } else {
                    currentUser = savedUser;
                    Toast.makeText(this, R.string.message_profile_saved,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_logout)
                .setMessage(R.string.message_logout_confirmation)
                .setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.profile_logout, (dialog, which) -> logout())
                .show();
    }

    private void logout() {
        // Clear the ID and role before leaving the authenticated part of the app.
        sessionManager.clearSession();
        openWelcomeAndClearBackStack();
    }

    private void openWelcomeAndClearBackStack() {
        Intent intent = new Intent(Profile.this, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}

package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.User;
import com.example.grabngo.util.SessionManager;
import com.example.grabngo.util.PasswordHasher;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private static final String TAG = "GrabNGoSession";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        TextView txtRegisterLink =
                findViewById(R.id.txtLoginRegisterLink);

        TextView txtForgotPassword =
                findViewById(R.id.txtLoginForgotPassword);

        btnLogin.setOnClickListener(v -> attemptLogin());

        txtRegisterLink.setOnClickListener(v ->
                startActivity(new Intent(this, Register.class)));

        txtForgotPassword.setOnClickListener(v ->
                Toast.makeText(this,
                        "Password recovery is not available yet.",
                        Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin() {

        String email = edtEmail.getText()
                .toString().trim().toLowerCase(Locale.ROOT);

        String password = edtPassword.getText().toString();

        if (email.isEmpty()) {
            edtEmail.setError("Enter your email.");
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Enter your password.");
            return;
        }

        btnLogin.setEnabled(false);

        databaseExecutor.execute(() -> {

            User user = null;
            Exception databaseError = null;

            try {
                AppDatabase database =
                        AppDatabase.getDatabase(getApplicationContext());

                user = database.userDao().getUserByEmail(email);

            } catch (Exception exception) {
                databaseError = exception;
                Log.e(TAG, "Login database error", exception);
            }

            final User authenticatedUser = user;
            final Exception loginError = databaseError;

            final boolean passwordMatches =
                    authenticatedUser != null
                            && authenticatedUser.getPassword() != null
                            && (
                            PasswordHasher.isHashed(authenticatedUser.getPassword())
                                    ? PasswordHasher.verify(
                                    password,
                                    authenticatedUser.getPassword()
                            )
                                    : authenticatedUser.getPassword().equals(password)
                    );

            boolean sessionSaved = false;
            String role = "";

            if (loginError == null
                    && authenticatedUser != null
                    && passwordMatches) {

                role = SessionManager.normalizeRole(
                        authenticatedUser.getRole());

                SessionManager sessionManager =
                        new SessionManager(getApplicationContext());

                sessionSaved = sessionManager.saveSession(
                        authenticatedUser.getUserId(),
                        role
                );

                Log.d(TAG,
                        "Session saved=" + sessionSaved
                                + ", userId=" + authenticatedUser.getUserId()
                                + ", role=" + role);
            }

            final String savedRole = role;
            final boolean savedSuccessfully = sessionSaved;

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                btnLogin.setEnabled(true);

                if (loginError != null) {
                    Toast.makeText(this,
                            "Database login error.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (authenticatedUser == null || !passwordMatches) {
                    Toast.makeText(this,
                            "Invalid email or password.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!savedSuccessfully) {
                    Toast.makeText(this,
                            "Unable to save login session.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Class<?> destination;

                if (SessionManager.ROLE_MANAGER.equals(savedRole)) {
                    destination = FoodList.class;
                } else {
                    destination = Home.class;
                }

                Intent intent = new Intent(
                        Login.this, destination);

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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

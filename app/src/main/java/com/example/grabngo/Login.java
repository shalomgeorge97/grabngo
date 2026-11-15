package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtRegisterLink = findViewById(R.id.txtLoginRegisterLink);
        TextView txtForgotPassword = findViewById(R.id.txtLoginForgotPassword);

        // Demo navigation: open Profile so all four assigned screens can be reviewed.
        // Real authentication and role-based navigation will be connected later.
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));

        txtRegisterLink.setOnClickListener(v ->
                startActivity(new Intent(this, Register.class)));

        txtForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, R.string.message_password_recovery_later,
                        Toast.LENGTH_SHORT).show());
    }
}
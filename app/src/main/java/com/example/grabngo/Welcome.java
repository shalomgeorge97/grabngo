package com.example.grabngo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnLogin = findViewById(R.id.btnWelcomeLogin);
        Button btnRegister = findViewById(R.id.btnWelcomeRegister);
        TextView txtContinueWithSso = findViewById(R.id.txtWelcomeSso);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class)));

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, Register.class)));

        // UI placeholder only. Provider sign-in will be added in the authentication phase.
        txtContinueWithSso.setOnClickListener(v ->
                Toast.makeText(this, R.string.message_sso_later, Toast.LENGTH_SHORT).show());
    }
}
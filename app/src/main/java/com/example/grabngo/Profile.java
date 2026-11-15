package com.example.grabngo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.grabngo.R;
import com.example.grabngo.Welcome;

public class Profile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button btnSaveProfile = findViewById(R.id.btnProfileSave);
        Button btnLogout = findViewById(R.id.btnProfileLogout);

        btnSaveProfile.setOnClickListener(v ->
                Toast.makeText(this, R.string.message_profile_save_later,
                        Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, Welcome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

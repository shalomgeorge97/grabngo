package com.example.grabngo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity {
    private Button btnCustomerRole;
    private Button btnManagerRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCustomerRole = findViewById(R.id.btnRegisterCustomer);
        btnManagerRole = findViewById(R.id.btnRegisterManager);
        Button btnUploadPhoto = findViewById(R.id.btnRegisterUploadPhoto);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView txtLoginLink = findViewById(R.id.txtRegisterLoginLink);

        // Customer is selected initially, matching the assigned customer registration screen.
        updateRoleSelection(true);
        btnCustomerRole.setOnClickListener(v -> updateRoleSelection(true));
        btnManagerRole.setOnClickListener(v -> updateRoleSelection(false));

        btnUploadPhoto.setOnClickListener(v ->
                Toast.makeText(this, R.string.message_photo_upload_later,
                        Toast.LENGTH_SHORT).show());

        btnRegister.setOnClickListener(v ->
                Toast.makeText(this, R.string.message_registration_later,
                        Toast.LENGTH_SHORT).show());

        txtLoginLink.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class)));
    }

    private void updateRoleSelection(boolean customerSelected) {
        btnCustomerRole.setBackgroundResource(customerSelected
                ? R.drawable.bg_role_selected : R.drawable.bg_role_unselected);
        btnManagerRole.setBackgroundResource(customerSelected
                ? R.drawable.bg_role_unselected : R.drawable.bg_role_selected);

        btnCustomerRole.setTextColor(getColor(customerSelected
                ? R.color.grabngo_green_dark : R.color.grabngo_muted));
        btnManagerRole.setTextColor(getColor(customerSelected
                ? R.color.grabngo_muted : R.color.grabngo_green_dark));
    }
}

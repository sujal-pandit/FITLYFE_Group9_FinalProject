package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextInputEditText etEmail, etPassword, etConfirmPassword, etName, etPhone, etDob;
    private Button btnSignUp;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        // DOB picker
        etDob.setOnClickListener(v -> showDatePicker());

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = safeText(etName);
                String dob = safeText(etDob);
                String phone = safeText(etPhone);
                String email = safeText(etEmail);
                String password = safeText(etPassword);
                String confirmPass = safeText(etConfirmPassword);

                if (name.isEmpty()) {
                    etName.setError("Name required");
                    etName.requestFocus();
                    return;
                }
                if (dob.isEmpty()) {
                    etDob.setError("Date of birth required");
                    etDob.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    etPhone.setError("Phone required");
                    etPhone.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    etEmail.setError("Email required");
                    etEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password required");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirmPass)) {
                    etConfirmPassword.setError("Passwords do not match");
                    etConfirmPassword.requestFocus();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();

                                        // build profile map instead of POJO
                                        Map<String, Object> profile = new HashMap<>();
                                        profile.put("uid", uid);
                                        profile.put("name", name);
                                        profile.put("email", email);
                                        profile.put("phone", phone);
                                        profile.put("birthday", dob);

                                        Toast.makeText(signup.this,
                                                "Saving profile to Firestore...",
                                                Toast.LENGTH_SHORT).show();

                                        db.collection("users")
                                                .document(uid)
                                                .set(profile)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(signup.this,
                                                            "Sign up successful",
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(signup.this, login.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(signup.this,
                                                            "Failed to save profile: "
                                                                    + (e.getMessage() != null ? e.getMessage() : ""),
                                                            Toast.LENGTH_LONG).show();
                                                });
                                    } else {
                                        Toast.makeText(signup.this,
                                                "Sign up successful, but user is null",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(signup.this,
                                            "Sign up failed: "
                                                    + (task.getException() != null
                                                    ? task.getException().getMessage()
                                                    : ""),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
            finish();
        });
    }

    private String safeText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    String date = String.format("%02d/%02d/%04d", d, m + 1, y);
                    etDob.setText(date);
                },
                year, month, day);

        dialog.show();
    }
}

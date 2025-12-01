package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    TextInputEditText etEmail, etPassword;
    Button btnLogin, googlelogin;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.tvSignUp);
        googlelogin = findViewById(R.id.btnGoogleSignUp);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

            if (email.isEmpty()) {
                etEmail.setError("Email required");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email");
                etEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password required");
                etPassword.requestFocus();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener((OnSuccessListener<AuthResult>) authResult -> {
                        Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener((OnFailureListener) e ->
                            Toast.makeText(login.this, e.getMessage() != null ? e.getMessage() : "Login Failed", Toast.LENGTH_SHORT).show()
                    );
        });

        signup.setOnClickListener(v -> {
            startActivity(new Intent(login.this, signup.class));
            finish();
        });
    }

    // 🔥 This is what makes "stay signed in" work
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // user already logged in -> go straight to main screen
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
        }
    }
}

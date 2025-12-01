package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class login extends AppCompatActivity {



    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 1001;
    TextInputEditText etEmail, etPassword;
    Button btnLogin, googlelogin;
    TextView signup;

    private GoogleSignInClient googleSignInClient;

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
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.tvSignUp);
        googlelogin = findViewById(R.id.btnGoogleSignUp);


        // Email / Password login
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

        //Sign up
        signup.setOnClickListener(v -> {
            startActivity(new Intent(login.this, signup.class));
            finish();
        });

        //Google Sign-In calling
        setupGoogleSignIn();

        googlelogin.setOnClickListener(v -> signInWithGoogle());
    }

    // GOOGLE SIGN-IN SETUP

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    @Deprecated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null) {
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // LINK GOOGLE ACCOUNT TO FIREBASE
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String idToken = acct.getIdToken();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Firebase user
                        String uid = auth.getCurrentUser().getUid();
                        String name = acct.getDisplayName() != null ? acct.getDisplayName() : "";
                        String email = acct.getEmail() != null ? acct.getEmail() : "";

                        // Build your userprofile object
                        // Phone and dob we don't have from Google, so leave empty/default
                        userprofile profile = new userprofile(uid, name, email, "", "");

                        // Save/merge into Firestore: users/{uid}
                        db.collection("users")
                                .document(uid)
                                .set(profile, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(login.this, "Google sign-in successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(login.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(login.this,
                                            "Failed to save Google profile: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(login.this,
                                "Firebase auth failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

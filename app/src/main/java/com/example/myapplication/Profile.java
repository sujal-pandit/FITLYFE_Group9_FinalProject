package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends Fragment {

    private TextView tvHeaderName, tvName, tvBirthday, tvPhone, tvEmail, tvPassword;
    private ImageView imgAvatar;

    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    public Profile() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ---------- UI refs ----------
        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvName       = view.findViewById(R.id.tvName);
        tvBirthday   = view.findViewById(R.id.tvBirthday);
        tvPhone      = view.findViewById(R.id.tvPhone);
        tvEmail      = view.findViewById(R.id.tvEmail);
        tvPassword   = view.findViewById(R.id.tvPassword);
        imgAvatar    = view.findViewById(R.id.imgAvatar);
        btnLogout = view.findViewById(R.id.btnlogout);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        uid = user.getUid();

        // Default email from auth (will be overwritten by Firestore if present)
        if (user.getEmail() != null) {
            tvEmail.setText(user.getEmail());
        }

        // Show masked password label (we never show real password)
        tvPassword.setText("********");

        // Load profile from Firestore
        loadProfile();



        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), login.class));
            requireActivity().finish();
        });


        return view;
    }

    private void loadProfile() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this::applyProfile)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load profile: " +
                                        (e.getMessage() != null ? e.getMessage() : ""),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void applyProfile(DocumentSnapshot snapshot) {
        if (!snapshot.exists()) {
            Toast.makeText(getContext(), "No profile data found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Field names must match what signup saves
        String name     = snapshot.getString("name");
        String phone    = snapshot.getString("phone");
        String birthday = snapshot.getString("birthday");
        String email    = snapshot.getString("email");

        // For debugging – you can temporarily uncomment this:
        // Toast.makeText(getContext(),
        //         "Loaded: " + name + " / " + phone + " / " + birthday,
        //         Toast.LENGTH_LONG).show();

        if (name != null && !name.isEmpty()) {
            tvHeaderName.setText(name);
            tvName.setText(name);
        }

        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText(phone);
        }

        if (birthday != null && !birthday.isEmpty()) {
            tvBirthday.setText(birthday);
        }

        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        }
    }
}

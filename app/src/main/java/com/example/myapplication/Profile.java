package com.example.myapplication;

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

    private TextView tvHeaderName, tvName, tvBirthday, tvPhone, tvInstagram, tvEmail, tvPassword;
    private ImageView imgAvatar;
    private ImageButton btnBack;
    private Button btnEditProfile;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    public Profile() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvName = view.findViewById(R.id.tvName);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPassword = view.findViewById(R.id.tvPassword);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnBack = view.findViewById(R.id.btnBack);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        uid = user.getUid();


        String email = user.getEmail();
        if (email != null) {
            tvEmail.setText(email);
        }

        loadProfile();

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnEditProfile.setOnClickListener(v -> {
            // TODO: open edit screen / enable fields
            Toast.makeText(getContext(), "Edit profile clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadProfile() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(this::applyProfile)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load profile: " +
                                        (e.getMessage() != null ? e.getMessage() : ""),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void applyProfile(DocumentSnapshot snapshot) {
        if (!snapshot.exists()) return;

        String name = snapshot.getString("name");
        String birthday = snapshot.getString("birthday");
        String phone = snapshot.getString("phone");
        String email = snapshot.getString("email");

        if (name != null && !name.isEmpty()) {
            tvHeaderName.setText(name);
            tvName.setText(name);
        }

        if (birthday != null && !birthday.isEmpty())
            tvBirthday.setText(birthday);

        if (phone != null && !phone.isEmpty())
            tvPhone.setText(phone);

        if (email != null && !email.isEmpty())
            tvEmail.setText(email);

        tvPassword.setText("********");
    }
}

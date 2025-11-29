package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends Fragment {

    private FirebaseAuth auth;
    private TextView tvProfileName, tvProfileEmail;
    private ImageView profileImage;
    private Button btnLogout, btnEditProfile;

    public Profile() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        profileImage = view.findViewById(R.id.profileImage);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // -------------------------------
        // Load User Information
        // -------------------------------
        if (user != null) {

            // Email
            if (user.getEmail() != null) {
                tvProfileEmail.setText(user.getEmail());
            }

            // Display name from Firebase user (if set)
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                tvProfileName.setText(user.getDisplayName());
            } else {
                tvProfileName.setText("User");
            }

        } else {
            tvProfileName.setText("Not Logged In");
            tvProfileEmail.setText("");
        }


        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            // Go back to login screen
            Intent intent = new Intent(getActivity(), login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            requireActivity().finish();
        });


        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile Coming Soon", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}

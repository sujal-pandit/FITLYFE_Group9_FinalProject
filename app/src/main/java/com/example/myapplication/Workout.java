package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Workout extends Fragment {

    ArrayList<Workout_Modal> workoutList = new ArrayList<>();
    Uri selectedImageUri = null;
    RecyclerView recyclerView;
    Workout_Adapter adapter;
    String selectedDay = "Monday";
    private ImageView currentImagePreview;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);


        recyclerView = view.findViewById(R.id.recWorkoutView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Workout_Adapter(getContext(), workoutList);
        recyclerView.setAdapter(adapter);

        Spinner daySpinner = view.findViewById(R.id.daySpinner);
        ImageView addWorkoutBtn = view.findViewById(R.id.addWorkoutBtn);
        ImageView removeWorkoutBtn = view.findViewById(R.id.removeWorkoutBtn);

        String[] days = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
        daySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,days));
        adapter.setOnDeleteListener(workout -> {
            deleteWorkoutFromFirebase(workout);
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDay = days[pos];
                loadWorkoutsFromFirebase();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        addWorkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWorkoutDialog();
            }
        });

        removeWorkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveWorkoutDialog();
            }
        });

        return view;
    }

    private void showRemoveWorkoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Workout")
                .setMessage("Long press a workout to delete it.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAddWorkoutDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_workout, null);

        EditText workoutNameInput = dialogView.findViewById(R.id.workoutNameInput);
        EditText calorieInput = dialogView.findViewById(R.id.calorieInput);
        MaterialButton pickImageBtn = dialogView.findViewById(R.id.pickImageBtn);
        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        MaterialButton addBtn = dialogView.findViewById(R.id.addBtn);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        pickImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                Workout.this.currentImagePreview = imagePreview;
                startActivityForResult(intent, 200);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = workoutNameInput.getText().toString();
                String calories = calorieInput.getText().toString();

                if (selectedImageUri != null) {
                    uploadImageAndSaveWorkout(name, calories,selectedImageUri);
                    selectedImageUri=null;
                } else {
                    saveWorkoutToFirebase(name, calories, null);
                }

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == getActivity().RESULT_OK && data!=null) {
            selectedImageUri = data.getData();

            if (currentImagePreview != null) {
                currentImagePreview.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImageAndSaveWorkout(String name, String calories,Uri imageUri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference ref = FirebaseStorage.getInstance().getReference("workoutImages/" + uid + "/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveWorkoutToFirebase(name, calories, uri.toString());
                }));
    }

    private void saveWorkoutToFirebase(String name, String calories, String imageUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("workouts").child(selectedDay);

        String key = ref.push().getKey();

        Workout_Modal modal = new Workout_Modal(imageUrl!=null?imageUrl:"", name, calories);

        ref.child(key).setValue(modal).addOnSuccessListener(a -> loadWorkoutsFromFirebase());
    }

    private void loadWorkoutsFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("workouts").child(selectedDay);

        workoutList.clear();

        ref.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot ds : snapshot.getChildren()) {
                Workout_Modal w = ds.getValue(Workout_Modal.class);
                if(w!=null){
                    workoutList.add(w);
                }
            }
            adapter.notifyDataSetChanged();
            Log.d("WorkoutFragment", "Snapshot count: " + snapshot.getChildrenCount());
        });


    }

    private void deleteWorkoutFromFirebase(Workout_Modal workout) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("workouts")
                .child(selectedDay);

        ref.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot ds : snapshot.getChildren()) {
                Workout_Modal w = ds.getValue(Workout_Modal.class);

                if (w != null &&
                        w.name.equals(workout.name) &&
                        w.calories.equals(workout.calories) &&
                        w.img.equals(workout.img)) {

                    ds.getRef().removeValue();
                    break;
                }
            }

            loadWorkoutsFromFirebase();
        });
    }


}
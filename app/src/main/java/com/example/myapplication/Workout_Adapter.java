package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Workout_Adapter extends RecyclerView.Adapter<Workout_Adapter.View_Holder> {
    Context context;
    ArrayList<Workout_Modal>  list;

    public Workout_Adapter(Context context, ArrayList<Workout_Modal> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workout_row, parent, false);
        View_Holder viewHolder = new View_Holder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Workout_Adapter.View_Holder holder, int position) {
        Workout_Modal item = list.get(position);

        holder.name.setText(item.name);
        holder.calories.setText(item.calories + " Calories");

        if (item.img != null && !item.img.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.img)
                    .placeholder(R.drawable.default_workout)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.default_workout);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {
        TextView name, calories;
        ImageView image;

        //Create Constructor
        public View_Holder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.workout_name);
            calories = itemView.findViewById(R.id.calories_burned);
            image = itemView.findViewById(R.id.workout_image);

        }
    }
}

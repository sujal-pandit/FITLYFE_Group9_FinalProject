package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Workout_Adapter extends RecyclerView.Adapter<Workout_Adapter.View_Holder> {
    Context context;
    ArrayList<Workout_Modal>  list;

    Workout_Adapter(Context context, ArrayList<Workout_Modal> list){
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
        holder.image.setImageResource(list.get(position).img);
        holder.name.setText(list.get(position).name);
        holder.calories.setText(list.get(position).calories);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {
        //Create Variables
        TextView name, calories;
        ImageView image;

        //Create Constructor
        public View_Holder(@NonNull View itemView) {
            super(itemView);
            //get views in the item of the viewholder

            name=itemView.findViewById(R.id.workout_name);
            calories = itemView.findViewById(R.id.calories_burned);
            image = itemView.findViewById(R.id.workout_image);

        }
    }
}

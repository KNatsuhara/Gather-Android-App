package com.example.Gather;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;

public class GM_RecyclerViewAdapter extends RecyclerView.Adapter<GM_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<GroupModel> groupModels;

    public GM_RecyclerViewAdapter(Context context, ArrayList<GroupModel> groupModels)
    {
        this.context = context;
        this.groupModels = groupModels;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (Giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new GM_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Assigning values to the views we created in the recycler_view_row layout file
        // based on the position of the recycler view

        holder.tvName.setText(groupModels.get(position).getGroupName());
        holder.tButton.setChecked(false);
    }

    @Override
    public int getItemCount() {
        // The recycler view just wants to know the number of items you want displayed
        return groupModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Kind of like the onCreate method
        // Grabbing the views from out recycler_view_row layout file

        TextView tvName;
        ToggleButton tButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.textView);
            tButton = itemView.findViewById(R.id.toggleButton);
        }
    }
}

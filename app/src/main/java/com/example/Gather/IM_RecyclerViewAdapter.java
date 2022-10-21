package com.example.Gather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IM_RecyclerViewAdapter extends RecyclerView.Adapter<IM_RecyclerViewAdapter.MyViewHolder> {

    private final IM_RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<ItemModel> itemModels;

    public IM_RecyclerViewAdapter(Context context, ArrayList<ItemModel> itemModels, IM_RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.itemModels = itemModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public IM_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (Giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inventory_recycler_view_row, parent, false);

        return new IM_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull IM_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigning values to the views we created in the recycler_view_row layout file
        // based on the position of the recycler view

        // Sets item name
        holder.iv_name.setText(itemModels.get(position).getName());
        // Sets item category
        holder.iv_category.setText(itemModels.get(position).getCategory());
        // Sets item quantity
        String quantityText = "Quantity: " + itemModels.get(position).getQuantity();
        holder.iv_quantity.setText(quantityText);
        // Sets item image OPTIONAL
        // holder.imageView.setImageResource(IMAGE);
    }

    @Override
    public int getItemCount() {
        // returns how many items there are in the recycler view
        return itemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Similar to the onCreate method
        // Grabs all the views from the inventory_recycler_view_row layout file

        ImageView imageView;
        TextView iv_name, iv_category, iv_quantity;

        public MyViewHolder(@NonNull View itemView, IM_RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_image_view);
            iv_name = itemView.findViewById(R.id.item_name_text_view);
            iv_category = itemView.findViewById(R.id.item_category_text_view);
            iv_quantity = itemView.findViewById(R.id.item_quantity_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}

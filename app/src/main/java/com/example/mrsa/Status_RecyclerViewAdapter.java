package com.example.mrsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Status_RecyclerViewAdapter extends RecyclerView.Adapter<Status_RecyclerViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<StatusModel> statusModels;

    public Status_RecyclerViewAdapter(Context context, ArrayList<StatusModel> statusModels) {
        this.context = context;
        this.statusModels = statusModels;
    }

    public Status_RecyclerViewAdapter() {

    }

    @NonNull
    @Override
    public Status_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout (provides look to rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new Status_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Status_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigns values to view that were created in recycler_view_row layout file
        // based on the position of the recycler view

        holder.operationImage.setImageResource(statusModels.get(position).getOperationImage());
        holder.deviceName.setText(statusModels.get(position).getDeviceName());
        holder.operationPerformed.setText(statusModels.get(position).getOperationPerformed());
        holder.operationTime.setText(statusModels.get(position).getOperationTime());

    }

    @Override
    public int getItemCount() {
        // the recycler view wants to know the number of items you want displayed (total number)
        return statusModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // retrieving the views from our recycler_view_row layout file
        // similar to onCreate method

        ImageView operationImage;
        TextView deviceName;
        TextView operationPerformed;
        TextView operationTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            operationImage = itemView.findViewById(R.id.operationImage);
            deviceName = itemView.findViewById(R.id.deviceName);
            operationPerformed = itemView.findViewById(R.id.operationPerformed);
            operationTime = itemView.findViewById(R.id.operationTime);
        }
    }
}

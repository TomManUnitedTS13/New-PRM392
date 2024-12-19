package com.example.a30shinehaircutapp.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a30shinehaircutapp.ChooseService.ServiceDetailActivity;
import com.example.a30shinehaircutapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private Context context;
    private List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        return new ServiceViewHolder(view, context, serviceList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.textViewServiceName.setText(service.getName());
        holder.textViewServicePrice.setText(service.getPrice() + " VND");
        holder.imageViewService.setImageResource(service.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewService;
        TextView textViewServiceName, textViewServicePrice;
        Context context;
        List<Service> serviceList;

        public ServiceViewHolder(@NonNull View itemView, Context context, List<Service> serviceList) {
            super(itemView);
            this.context = context;
            this.serviceList = serviceList;
            imageViewService = itemView.findViewById(R.id.imageViewService);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewServicePrice = itemView.findViewById(R.id.textViewServicePrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Service service = serviceList.get(position);
                Intent intent = new Intent(context, ServiceDetailActivity.class);
                intent.putExtra("serviceName", service.getName());
                intent.putExtra("servicePrice", service.getPrice());
                intent.putExtra("serviceImageUrl", service.getImageUrl());
                intent.putExtra("serviceDescription", service.getDescription());
                context.startActivity(intent);
            }
        }
    }

}

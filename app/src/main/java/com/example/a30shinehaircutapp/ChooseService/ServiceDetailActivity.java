package com.example.a30shinehaircutapp.ChooseService;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a30shinehaircutapp.R;

public class ServiceDetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        ImageView imageViewService = findViewById(R.id.imageViewServiceDetail);
        TextView textViewServiceName = findViewById(R.id.textViewServiceNameDetail);
        TextView textViewServicePrice = findViewById(R.id.textViewServicePriceDetail);
        TextView textViewServiceDescription = findViewById(R.id.textViewServiceDescriptionDetail);

        // Get data from intent
        String serviceName = getIntent().getStringExtra("serviceName");
        int servicePrice = getIntent().getIntExtra("servicePrice", 0);
        int serviceImageUrl = getIntent().getIntExtra("serviceImageUrl", 0);
        String serviceDescription = getIntent().getStringExtra("serviceDescription");

        // Set data to views
        textViewServiceName.setText(serviceName);
        textViewServicePrice.setText(servicePrice + " VND");
        imageViewService.setImageResource(serviceImageUrl);
        textViewServiceDescription.setText(serviceDescription);
    }
}

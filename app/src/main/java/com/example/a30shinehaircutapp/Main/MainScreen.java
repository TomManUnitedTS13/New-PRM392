package com.example.a30shinehaircutapp.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a30shinehaircutapp.R;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainScreen extends BaseScreen {
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    ServiceAdapter serviceAdapter;
    List<Service> serviceList;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main_screen, findViewById(R.id.activity_content));
        viewFlipper = findViewById(R.id.viewFlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recyclerView);
        ActionViewFlipper();
        serviceList = new ArrayList<>();
        // Thiết lập Adapter cho RecyclerView
        serviceAdapter = new ServiceAdapter(this, serviceList);
        recyclerViewManHinhChinh.setAdapter(serviceAdapter);
        recyclerViewManHinhChinh.setLayoutManager(new GridLayoutManager(this, 3));
        loadServices();
    }

    private void ActionViewFlipper() {
        loadBackgroundImages();
    }

    private void loadBackgroundImages() {
        String url = "http://10.33.42.195/prm392/BarberShop/getBackgroundImages.php";
        // Khởi tạo RequestQueue nếu chưa có
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        // Sử dụng StringRequest để nhận phản hồi dạng chuỗi
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    ArrayList<String> mangQuangCao = new ArrayList<>();
                    try {
                        // Loại bỏ tiền tố "connected" nếu nó xuất hiện ở đầu chuỗi JSON
                        String jsonString = response.startsWith("connected") ? response.replaceFirst("connected", "") : response;

                        // Chuyển chuỗi JSON thành JSONArray
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String imageUrl = jsonObject.getString("image_url");
                            mangQuangCao.add(imageUrl);
                        }
                        setupViewFlipper(mangQuangCao);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(stringRequest);
    }


    private void setupViewFlipper(ArrayList<String> mangQuangCao) {
        for (String imageUrl : mangQuangCao) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.get().load(imageUrl).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
        Animation animation_slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation animation_slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(animation_slide_in);
        viewFlipper.setOutAnimation(animation_slide_out);
    }

    private void loadServices() {
        // Khởi tạo RequestQueue nếu chưa có
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        String url = "http://10.33.42.195/prm392/BarberShop/getServices.php";

        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        List<Service> services = new ArrayList<>();
                        String jsonString = response.startsWith("connected") ? response.replaceFirst("connected", "") : response;
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String description = jsonObject.getString("description");
                            int price = jsonObject.getInt("price");
                            int duration = jsonObject.getInt("duration");
                            String imageName = jsonObject.getString("image_url").replace(".jpg", "");
                            @SuppressLint("DiscouragedApi")
                            int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

                            services.add(new Service(name, description, price, duration, imageResId)); // Giữ nguyên kiểu dữ liệu
                        }

                        serviceList.clear();
                        serviceList.addAll(services);
                        serviceAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(stringRequest2);
    }
}
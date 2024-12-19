package com.example.a30shinehaircutapp.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.a30shinehaircutapp.Account.AccountSettingsActivity;
import com.example.a30shinehaircutapp.Account.HaircutHistoryActivity;
import com.example.a30shinehaircutapp.Account.LoyalCustomersActivity;
import com.example.a30shinehaircutapp.ChooseService.ChooseServiceActivity;
import com.example.a30shinehaircutapp.Login_Register.ForgotPasswordScreen;
import com.example.a30shinehaircutapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class BaseScreen extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    TextView usernameTextView, userRankTextView;
    ImageView scissorsImage;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_screen);
        Anh_xa();
        ActionBar();
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String membership_tier = intent.getStringExtra("membership_tier");

        assert username != null;
        setUserData(username, membership_tier);

        scissorsImage.setOnClickListener(v -> {
            // Mở màn hình quên mật khẩu
            Intent intent2 = new Intent(this, ChooseServiceActivity.class);
            startActivity(intent2);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks
                int itemId = item.getItemId();

                if (itemId == R.id.nav_choose_service) {
                    startActivity(new Intent(BaseScreen.this, ChooseServiceActivity.class));
                } else if (itemId == R.id.nav_haircut_history) {
                    startActivity(new Intent(BaseScreen.this, HaircutHistoryActivity.class));
                } else if (itemId == R.id.nav_loyal_customers) {
                    startActivity(new Intent(BaseScreen.this, LoyalCustomersActivity.class));
                }

                drawerLayout.closeDrawers();  // Đóng Drawer sau khi chọn
                return true;
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(BaseScreen.this, MainScreen.class);
                    intent.putExtra("username", username);
                    intent.putExtra("membership_tier", membership_tier);
                    startActivity(intent);
                } else if (itemId == R.id.nav_schedule) {
                    Intent intent2 = new Intent(BaseScreen.this, ChooseServiceActivity.class);
                    intent2.putExtra("username", username);
                    intent2.putExtra("membership_tier", membership_tier);
                    startActivity(intent2);
                } else if (itemId == R.id.nav_account) {
                    Intent intent3 = new Intent(BaseScreen.this, AccountSettingsActivity.class);
                    intent3.putExtra("username", username);
                    intent3.putExtra("membership_tier", membership_tier);
                    startActivity(intent3);
                }

                drawerLayout.closeDrawers();  // Đóng Drawer sau khi chọn
                return true;
            }
        });
    }
    private void ActionBar(){
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void Anh_xa(){
        toolbar = findViewById(R.id.toolbarManHinhChinh);
        navigationView = findViewById(R.id.navigationView);
        listViewManHinhChinh = findViewById(R.id.listViewManHinhChinh);
        drawerLayout = findViewById(R.id.drawerLayout);
        scissorsImage = findViewById(R.id.scissors_icon);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setUserData(String username, String userRank) {
        usernameTextView = findViewById(R.id.username);
        userRankTextView = findViewById(R.id.user_rank);

        if (username.length() <= 6){
            usernameTextView.setText(String.format("Tên KH: %s", username));
        }
        else{
            usernameTextView.setText(String.format("Tên KH: %s..", username.substring(0,6)));
        }
        userRankTextView.setText(String.format("Hạng: %s", userRank));
    }
}
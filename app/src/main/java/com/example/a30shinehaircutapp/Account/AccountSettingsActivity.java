package com.example.a30shinehaircutapp.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a30shinehaircutapp.Login_Register.LoginScreen;
import com.example.a30shinehaircutapp.Main.BaseScreen;
import com.example.a30shinehaircutapp.R;

public class AccountSettingsActivity extends BaseScreen {

    private TextView userNameTextView;
    private Button userInfoButton, haircutHistoryButton, membershipButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_account_settings, findViewById(R.id.activity_content));

        userNameTextView = findViewById(R.id.user_name);
        userInfoButton = findViewById(R.id.user_info_button);
        haircutHistoryButton = findViewById(R.id.haircut_history_button);
        membershipButton = findViewById(R.id.membership_button);
        logoutButton = findViewById(R.id.logout_button);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String membership_tier = intent.getStringExtra("membership_tier");
        if (username != null) {
            userNameTextView.setText(username);
        }

        userInfoButton.setOnClickListener(v -> {
            // Navigate to user info screen
            Intent userInfoIntent = new Intent(AccountSettingsActivity.this, AccountInformationActivity.class);
            userInfoIntent.putExtra("username", username);
            userInfoIntent.putExtra("membership_tier", membership_tier);
            startActivity(userInfoIntent);
        });

        haircutHistoryButton.setOnClickListener(v -> {
            // Navigate to haircut history screen
            Intent haircutHistoryIntent = new Intent(AccountSettingsActivity.this, HaircutHistoryActivity.class);
            haircutHistoryIntent.putExtra("username", username);
            startActivity(haircutHistoryIntent);
        });

        membershipButton.setOnClickListener(v -> {
            // Navigate to membership screen
            Intent membershipIntent = new Intent(AccountSettingsActivity.this, LoyalCustomersActivity.class);
            membershipIntent.putExtra("username", username);
            startActivity(membershipIntent);
        });

        logoutButton.setOnClickListener(v -> {
            // Handle logout
            // For example, clear user session and navigate to login screen
            Intent logoutIntent = new Intent(AccountSettingsActivity.this, LoginScreen.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }
}
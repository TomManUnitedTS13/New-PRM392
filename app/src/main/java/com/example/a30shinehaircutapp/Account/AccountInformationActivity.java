package com.example.a30shinehaircutapp.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a30shinehaircutapp.Login_Register.LoginScreen;
import com.example.a30shinehaircutapp.Main.BaseScreen;
import com.example.a30shinehaircutapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;

public class AccountInformationActivity extends BaseScreen {

    private TextView accountNameTextView, accountEmailTextView, accountPhoneTextView, accountBirthdateTextView;
    private Button accountEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_account_information, findViewById(R.id.activity_content));

        // Initialize views
        accountNameTextView = findViewById(R.id.account_name);
        accountEmailTextView = findViewById(R.id.account_email);
        accountPhoneTextView = findViewById(R.id.account_phone);
        accountBirthdateTextView = findViewById(R.id.account_birthdate);
        accountEditButton = findViewById(R.id.edit_button);

        // Get username from intent
        String username = getIntent().getStringExtra("username");
        String membership_tier = getIntent().getStringExtra("membership_tier");

        // Fetch account details
        if (username != null) {
            new FetchAccountDetailsTask().execute(username);
        }

        accountEditButton.setOnClickListener(v -> {
            // Navigate to user info screen
            Intent userInfoIntent = new Intent(AccountInformationActivity.this, AccountInfoEdit.class);
            userInfoIntent.putExtra("username", username);
            userInfoIntent.putExtra("membership_tier", membership_tier);
            startActivity(userInfoIntent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class FetchAccountDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/getAccountsFromUsername.php?username=" + username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(AccountInformationActivity.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            String jsonString = result.replaceFirst("connected", "");
            try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    JSONObject account = jsonArray.getJSONObject(0);

                    String accountName = account.getString("full_name");
                    String accountEmail = account.getString("email");
                    String accountPhone = account.getString("phone_number");
                    String accountBirthdate = account.getString("dob");

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedBirthdate = "";
                    try {
                        java.util.Date utilDate = originalFormat.parse(accountBirthdate);

                        // Convert java.util.Date to java.sql.Date
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                        // Format the date to "dd/MM/yyyy" format
                        formattedBirthdate = desiredFormat.format(sqlDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    accountNameTextView.setText("Tên tài khoản: " + accountName);
                    accountEmailTextView.setText("Email: " + accountEmail);
                    accountPhoneTextView.setText("Số điện thoại: " + accountPhone);
                    accountBirthdateTextView.setText("Ngày sinh: " + formattedBirthdate);
            } catch (Exception e) {
                    e.printStackTrace();
            }
        }
    }
}

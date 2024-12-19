package com.example.a30shinehaircutapp.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a30shinehaircutapp.Login_Register.LoginScreen;
import com.example.a30shinehaircutapp.Login_Register.RegisterScreen;
import com.example.a30shinehaircutapp.Main.BaseScreen;
import com.example.a30shinehaircutapp.Main.MainScreen;
import com.example.a30shinehaircutapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AccountInfoEdit extends BaseScreen {

    private EditText accountNameEditText, passwordEditText, fullNameEditText, emailEditText, phoneEditText, birthdateEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_account_info_edit, findViewById(R.id.activity_content));

        accountNameEditText = findViewById(R.id.edit_account_name);
        passwordEditText = findViewById(R.id.edit_password);
        fullNameEditText = findViewById(R.id.edit_fullName);
        emailEditText = findViewById(R.id.edit_email);
        phoneEditText = findViewById(R.id.edit_phone);
        birthdateEditText = findViewById(R.id.edit_birthdate);
        saveButton = findViewById(R.id.save_button);

        String username = getIntent().getStringExtra("username");
        String membership_tier = getIntent().getStringExtra("membership_tier");

        if (username != null) {
            new FetchAccountDetailsTask().execute(username);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccountInfo(username, membership_tier);
            }
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
                Toast.makeText(AccountInfoEdit.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            String jsonString = result.replaceFirst("connected", "");
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONObject account = jsonArray.getJSONObject(0);

                String accountName = account.getString("username");
                String password = account.getString("password");
                String fullName = account.getString("full_name");
                String email = account.getString("email");
                String phone = account.getString("phone_number");
                String birthdate = account.getString("dob");

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedBirthdate = "";
                try {
                    java.util.Date date = originalFormat.parse(birthdate);
                    formattedBirthdate = desiredFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                accountNameEditText.setText(accountName);
                passwordEditText.setText(password);
                emailEditText.setText(email);
                fullNameEditText.setText(fullName);
                phoneEditText.setText(phone);
                birthdateEditText.setText(formattedBirthdate);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAccountInfo(String username, String membership_tier) {
        String accountName = accountNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String birthdate = birthdateEditText.getText().toString().trim();

        if (!isValidDateOfBirth(birthdate)) {
            Toast.makeText(this,
                    "Ngày sinh không hợp lệ."
                    , Toast.LENGTH_LONG).show();
            return;
        }

        // Validate input
        if (accountName.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || birthdate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || password.length() > 20){
            Toast.makeText(this, "Mật khẩu từ 6 đến 20 ký tự.", Toast.LENGTH_SHORT).show();
        }
        if (phone.length() < 10 || phone.length() > 11){
            Toast.makeText(this, "Số điện thoại từ 10-11 chữ số.", Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("SimpleDateFormat")
        java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat")
        java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date sqlDate = null;

        try {
            // Parse the date from "dd/MM/yyyy" format
            java.util.Date parsedDate = inputFormat.parse(birthdate);
            assert parsedDate != null;

            // Format the date to "yyyy-MM-dd" format
            String formattedDate = outputFormat.format(parsedDate);

            // Convert to java.sql.Date
            sqlDate = java.sql.Date.valueOf(formattedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        String newDob = sqlDate.toString();

        // Send data to server
        new UpdateAccountTask().execute(accountName, password, fullName, email, phone, newDob, username, membership_tier);
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateAccountTask extends AsyncTask<String, Void, String> {
        private String accountName;
        private String membership_tier;
        @Override
        protected String doInBackground(String... params) {
            accountName = params[0];
            String password = params[1];
            String fullName = params[2];
            String email = params[3];
            String phone = params[4];
            String birthdate = params[5];
            String username = params[6];
            membership_tier = params[7];

            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/updateAccount.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare POST data
                Map<String, String> postData = new HashMap<>();
                postData.put("username", accountName);
                postData.put("password", password);
                postData.put("fullName", fullName);
                postData.put("email", email);
                postData.put("phone", phone);
                postData.put("birthdate", birthdate);
                postData.put("oldUsername", username);

                // Write data to output stream
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postData));
                writer.flush();
                writer.close();
                os.close();

                // Read response from server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return result.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(AccountInfoEdit.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            String jsonString = result.replaceFirst("connected", "");
            try {
                JSONObject jsonResponse = new JSONObject(jsonString);
                String success = jsonResponse.getString("success");

                if (success.equals("1")) {
                    Toast.makeText(AccountInfoEdit.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountInfoEdit.this, MainScreen.class);
                    intent.putExtra("username", accountName);
                    intent.putExtra("membership_tier", membership_tier);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AccountInfoEdit.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AccountInfoEdit.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper method to convert Map to POST data string
    private String getPostDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() != 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }

    private boolean isValidDateOfBirth(String dob) {
        String regex = "^\\d{2}/\\d{2}/\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dob);

        if (!matcher.matches()) {
            return false;
        }

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            Date date = dateFormat.parse(dob);
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);

            Calendar minDate = Calendar.getInstance();
            minDate.set(1900, Calendar.JANUARY, 1);

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, -5);

            return !calendar.before(minDate) && !calendar.after(maxDate);
        } catch (java.text.ParseException e) {
            return false;
        }
    }
}
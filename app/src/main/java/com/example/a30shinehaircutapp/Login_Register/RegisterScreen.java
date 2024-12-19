package com.example.a30shinehaircutapp.Login_Register;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a30shinehaircutapp.Main.MainScreen;
import com.example.a30shinehaircutapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegisterScreen extends AppCompatActivity {

    private EditText accountNameText, passwordText, fullNameText, dobText, emailText, phoneNumberText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Ánh xạ các EditText và Button
        accountNameText = findViewById(R.id.accountNameText);
        passwordText = findViewById(R.id.passwordText);
        fullNameText = findViewById(R.id.fullNameText);
        dobText = findViewById(R.id.dobText);
        emailText = findViewById(R.id.emailText);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        registerButton = findViewById(R.id.button2);

        // Đăng ký sự kiện khi nhấn nút Đăng ký
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = accountNameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String fullName = fullNameText.getText().toString().trim();
        String dob = dobText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String phoneNumber = phoneNumberText.getText().toString().trim();
        String membershipId = "3"; // giá trị mặc định cho membership_id

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        // Get the current date
        Date currentDate = new Date();

        // Format the current date
        String registerDate = dateFormat.format(currentDate);

        if (!isValidDateOfBirth(dob)) {
            Toast.makeText(this,
                    "Ngày sinh không hợp lệ."
                    , Toast.LENGTH_LONG).show();
            return;
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date sqlDate = null;

        try {
            // Parse the date from "dd/MM/yyyy" format
            java.util.Date parsedDate = inputFormat.parse(dob);
            assert parsedDate != null;

            // Format the date to "yyyy-MM-dd" format
            String formattedDate = outputFormat.format(parsedDate);

            // Convert to java.sql.Date
            sqlDate = java.sql.Date.valueOf(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Kiểm tra dữ liệu hợp lệ
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || dob.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 20){
            Toast.makeText(this, "Mật khẩu từ 6 đến 20 ký tự.", Toast.LENGTH_SHORT).show();
        }
        if (phoneNumber.length() < 10 || phoneNumber.length() > 11){
            Toast.makeText(this, "Số điện thoại từ 10-11 chữ số.", Toast.LENGTH_SHORT).show();
        }
        // Tạo một thread riêng để thực hiện yêu cầu HTTP
        Date finalDate = sqlDate;
        new Thread(() -> {
            try {
                // URL của API PHP
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/barbershopRegister.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Dữ liệu POST
                Map<String, String> postData = new HashMap<>();
                postData.put("username", username);
                postData.put("password", password);
                postData.put("fullName", fullName);
                postData.put("dob", finalDate.toString());
                postData.put("email", email);
                postData.put("phoneNumber", phoneNumber);
                postData.put("membershipId", membershipId);
                postData.put("register_date", registerDate.toString());

                // Ghi dữ liệu vào OutputStream
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postData));
                writer.flush();
                writer.close();
                os.close();

                // Xử lý phản hồi từ server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Đọc phản hồi JSON từ server
                    String response = new BufferedReader
                            (new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining());
                    String jsonString = response.replaceFirst("connected", "");
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    String success = jsonResponse.getString("success");

                    // Hiển thị kết quả đăng ký
                    runOnUiThread(() -> {
                        if (success.equals("1")) {
                            Toast.makeText(RegisterScreen.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterScreen.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterScreen.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterScreen.this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Phương thức chuyển Map dữ liệu thành chuỗi POST
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
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
        } catch (ParseException e) {
            return false;
        }
    }
}

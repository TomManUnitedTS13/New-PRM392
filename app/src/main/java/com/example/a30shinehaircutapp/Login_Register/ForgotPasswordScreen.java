package com.example.a30shinehaircutapp.Login_Register;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a30shinehaircutapp.R;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ForgotPasswordScreen extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, password1EditText, password2EditText;
    private Button updatePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_screen);

        usernameEditText = findViewById(R.id.nameText);
        emailEditText = findViewById(R.id.emailText);
        password1EditText = findViewById(R.id.password1Text);
        password2EditText = findViewById(R.id.password2Text);
        updatePasswordButton = findViewById(R.id.buttonUpdatePassword);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password1 = password1EditText.getText().toString().trim();
                String password2 = password2EditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                    Toast.makeText(ForgotPasswordScreen.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.length() < 6 || password1.length() > 20){
                    Toast.makeText(ForgotPasswordScreen.this, "Mật khẩu từ 6 đến 20 ký tự.", Toast.LENGTH_SHORT).show();
                }

                if (!password1.equals(password2)) {
                    Toast.makeText(ForgotPasswordScreen.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thực hiện yêu cầu PUT lên server
                new UpdatePasswordTask(username, email, password1).execute();
            }
        });
    }
    private class UpdatePasswordTask extends AsyncTask<Void, Void, String> {
        private String username, email, password;

        public UpdatePasswordTask(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/forgotPassword.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                // Tạo chuỗi dữ liệu gửi lên server
                String data = "username=" + username + "&email=" + email + "&password=" + password;

                // Gửi dữ liệu
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Đọc phản hồi từ server
                    java.io.InputStream is = connection.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    is.close();

                    return response;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    String jsonString = response.replaceFirst("connected", "");
                    // Phân tích JSON phản hồi
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        Toast.makeText(ForgotPasswordScreen.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordScreen.this, LoginScreen.class);
                        startActivity(intent);
                        finish(); // Đóng màn hình hiện tại
                    } else {
                        Toast.makeText(ForgotPasswordScreen.this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ForgotPasswordScreen.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ForgotPasswordScreen.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
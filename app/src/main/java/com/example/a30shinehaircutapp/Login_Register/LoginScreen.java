package com.example.a30shinehaircutapp.Login_Register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a30shinehaircutapp.Main.MainScreen;
import com.example.a30shinehaircutapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.nameText)).getText().toString();
            String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();
            checkLogin(username, password);
        });

        TextView forgotPasswordTextView = findViewById(R.id.textView4);
        forgotPasswordTextView.setOnClickListener(v -> {
            // Mở màn hình quên mật khẩu
            Intent intent = new Intent(this, ForgotPasswordScreen.class);
            startActivity(intent);
        });

        TextView registerTextView = findViewById(R.id.textView5);
        registerTextView.setOnClickListener(v -> {
            // Mở màn hình quên mật khẩu
            Intent intent = new Intent(this, RegisterScreen.class);
            startActivity(intent);
        });
    }

    private void checkLogin(String username, String password) {
        new LoginTask().execute(username, password);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoginTask extends AsyncTask<String, Void, String> {
        private String username;
        @Override
        protected String doInBackground(String... params) {
            username = params[0];
            String password = params[1];

            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/barbershopLogin.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String data = "username=" + username + "&password=" + password;
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

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

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(LoginScreen.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            String jsonString = result.replaceFirst("connected", "");
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String success = jsonObject.getString("success");

                if (success.equals("1")) {
                    String membershipTier = jsonObject.getJSONArray("login")
                            .getJSONObject(0).getString("membership_tier");
                    Toast.makeText(LoginScreen.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginScreen.this, MainScreen.class);
                    intent.putExtra("username", username);
                    intent.putExtra("membership_tier", membershipTier);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginScreen.this, "Tên tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginScreen.this, "Lỗi phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

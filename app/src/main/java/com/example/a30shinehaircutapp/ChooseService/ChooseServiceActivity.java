package com.example.a30shinehaircutapp.ChooseService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a30shinehaircutapp.Main.BaseScreen;
import com.example.a30shinehaircutapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChooseServiceActivity extends BaseScreen {

    private Spinner salonSpinner, servicesSpinner, timeSpinner, stylistSpinner;
    private TextView selectSalonLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_choose_service, findViewById(R.id.activity_content));

        salonSpinner = findViewById(R.id.select_salon_spinner);
        servicesSpinner = findViewById(R.id.select_services_spinner);
        timeSpinner = findViewById(R.id.select_time_spinner);
        stylistSpinner = findViewById(R.id.select_stylist_spinner);
        selectSalonLabel = findViewById(R.id.select_salon_label);


        new FetchBranchesDataTask().execute();
        new FetchServicesDataTask().execute();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class FetchServicesDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/getServices.php");
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

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(ChooseServiceActivity.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            String jsonString = result.replaceFirst("connected", "");
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                ArrayList<String> services = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject service = jsonArray.getJSONObject(i);
                    services.add(service.getString("service_name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (ChooseServiceActivity.this, android.R.layout.simple_spinner_item, services);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                servicesSpinner.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class FetchBranchesDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.33.42.195/prm392/BarberShop/getBranches.php");
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

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(ChooseServiceActivity.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<String> branches = new ArrayList<>();
                ArrayList<String> addresses = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject branch = jsonArray.getJSONObject(i);
                    branches.add(branch.getString("branch_name"));
                    addresses.add(branch.getString("address"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChooseServiceActivity.this, android.R.layout.simple_spinner_item, branches);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                salonSpinner.setAdapter(adapter);

                // Display the address of the selected branch
                salonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectSalonLabel.setText("Chọn salon: " + addresses.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectSalonLabel.setText("Chọn salon");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class RateUs extends AppCompatActivity {
    Button button;
    String Username;
    String Password;
    RatingBar ratingbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        button = findViewById(R.id.button);
        ratingbar = findViewById(R.id.ratingBar);

        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, 0);
        Username = sharedPreferences.getString("user", "");
        Password = sharedPreferences.getString("password", "");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Handle home item selection
                        Intent intent2 = new Intent(RateUs.this, AboutUs.class);
                        startActivity(intent2);
                        return true;
                    case R.id.menu_search:
                        // Handle search item selection
                        Intent intent = new Intent(RateUs.this, Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_notifications:
                        // Handle notifications item selection
                        return true;
                    case R.id.menu_profile:
                        // Handle profile item selection
                        Intent intent3 = new Intent(RateUs.this, Community.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.menu_notifications).setChecked(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating = String.valueOf(ratingbar.getRating());
                insertRating(Username, rating);
            }
        });
    }

    private void insertRating(String username, String rating) {
        ProgressDialog progressDialog = new ProgressDialog(RateUs.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://tsm.ecssofttech.com/Library/api/RC_Rating.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("User Already Exist")) {
                    Toast.makeText(RateUs.this, "Your Rating Already Exists", Toast.LENGTH_SHORT).show();
                } else if (response.equalsIgnoreCase("Record Inserted Successfully")) {
                    Toast.makeText(RateUs.this, "Successfully Rated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RateUs.this, RateUs.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RateUs.this, "Failed", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RateUs.this, "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Username", username);
                params.put("Rating", rating);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RateUs.this);
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in2 = new Intent(RateUs.this, Home.class);
        startActivity(in2);
        finish();
    }
}

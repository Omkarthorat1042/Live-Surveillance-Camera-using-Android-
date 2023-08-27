package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddCommunity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nav;
    private Toolbar toolbar;

    private String user, password;

    private BottomNavigationView bottomNavigationView;

    private EditText n1, n2, n3, n4, n5, m1, m2, m3, m4, m5;
    private Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_community);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav = findViewById(R.id.nav);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_Community:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_Home:
                        Intent intent3 = new Intent(AddCommunity.this, Home.class);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.menu_Scan:
                        Intent intent2 = new Intent(AddCommunity.this, Scan.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.menu_Logout:
                        SharedPreferences.Editor editor = getSharedPreferences(Login.PREFS_NAME, 0).edit();
                        editor.remove("user");
                        editor.remove("password");
                        editor.apply();
                        overridePendingTransition(0, 0);
                        finish();
                        Toast.makeText(AddCommunity.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(AddCommunity.this, Login.class);
                        startActivity(intent1);
                        finish();
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Handle home item selection
                        Intent intent2 = new Intent(AddCommunity.this, AboutUs.class);
                        startActivity(intent2);
                        return true;

                    case R.id.menu_search:
                        // Handle search item selection
                        Intent intent = new Intent(AddCommunity.this, Home.class);
                        startActivity(intent);
                        return true;

                    case R.id.menu_notifications:
                        // Handle notifications item selection
                        Intent intent1 = new Intent(AddCommunity.this, RateUs.class);
                        startActivity(intent1);
                        return true;

                    case R.id.menu_profile:
                        // Handle profile item selection
                        Intent intent3 = new Intent(AddCommunity.this, Community.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, 0);
        user = sharedPreferences.getString("user", "");
        password = sharedPreferences.getString("password", "");

        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        n3 = findViewById(R.id.n3);
        n4 = findViewById(R.id.n4);
        n5 = findViewById(R.id.n5);

        b1 = findViewById(R.id.b1);

        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        m3 = findViewById(R.id.m3);
        m4 = findViewById(R.id.m4);
        m5 = findViewById(R.id.m5);

        String Name = sharedPreferences.getString("Name", "");
        String Email = sharedPreferences.getString("Email", "");
        String Address = sharedPreferences.getString("Address", "");

        NavigationView navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_name = headerView.findViewById(R.id.nav_name);
        TextView nav_mobile = headerView.findViewById(R.id.nav_mobile);
        TextView nav_email = headerView.findViewById(R.id.nav_email);
        TextView nav_address = headerView.findViewById(R.id.nav_address);
        nav_name.setText(Name);
        nav_mobile.setText(user);
        nav_email.setText(Email);
        nav_address.setText(Address);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name1 = n1.getText().toString().trim();
                String Mobile1 = m1.getText().toString().trim();
                String Name2 = n2.getText().toString().trim();
                String Mobile2 = m2.getText().toString().trim();
                String Name3 = n3.getText().toString().trim();
                String Mobile3 = m3.getText().toString().trim();
                String Name4 = n4.getText().toString().trim();
                String Mobile4 = m4.getText().toString().trim();
                String Name5 = n5.getText().toString().trim();
                String Mobile5 = m5.getText().toString().trim();

                if (validateFields(Name1, Mobile1, Name2, Mobile2, Name3, Mobile3, Name4, Mobile4, Name5, Mobile5)) {
                    // Start the task to add the community
                    AddCommunityTask addCommunityTask = new AddCommunityTask();
                    addCommunityTask.execute(Name1, Mobile1, Name2, Mobile2, Name3, Mobile3, Name4, Mobile4, Name5, Mobile5);
                }
            }
        });
    }

    private boolean validateFields(String name1, String mobile1, String name2, String mobile2, String name3, String mobile3, String name4, String mobile4, String name5, String mobile5) {
        if (name1.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Name1", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile1.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Mobile Number1", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name2.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Name2", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile2.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Mobile Number2", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name3.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Name3", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile3.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Mobile Number3", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name4.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Name4", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile4.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Mobile Number4", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name5.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Name5", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile5.isEmpty()) {
            Toast.makeText(AddCommunity.this, "Enter Mobile Number5", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile1.length() != 10 || mobile2.length() != 10 || mobile3.length() != 10 || mobile4.length() != 10 || mobile5.length() != 10) {
            Toast.makeText(AddCommunity.this, "Mobile Number must have a length of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Pattern.matches("^[6-9]\\d{9}$", mobile1) || !Pattern.matches("^[6-9]\\d{9}$", mobile2) || !Pattern.matches("^[6-9]\\d{9}$", mobile3) || !Pattern.matches("^[6-9]\\d{9}$", mobile4) || !Pattern.matches("^[6-9]\\d{9}$", mobile5)) {
            Toast.makeText(AddCommunity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class AddCommunityTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddCommunity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String name1 = strings[0];
            String mobile1 = strings[1];
            String name2 = strings[2];
            String mobile2 = strings[3];
            String name3 = strings[4];
            String mobile3 = strings[5];
            String name4 = strings[6];
            String mobile4 = strings[7];
            String name5 = strings[8];
            String mobile5 = strings[9];

            try {
                // Simulating a network request here
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Perform the API request and return the response
            // Replace "your_api_endpoint" with the actual API endpoint
            String apiEndpoint = "your_api_endpoint";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(AddCommunity.this, response, Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(AddCommunity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Set the parameters for the POST request
                    Map<String, String> params = new HashMap<>();
                    params.put("user", user);
                    params.put("password", password);
                    params.put("name1", name1);
                    params.put("mobile1", mobile1);
                    params.put("name2", name2);
                    params.put("mobile2", mobile2);
                    params.put("name3", name3);
                    params.put("mobile3", mobile3);
                    params.put("name4", name4);
                    params.put("mobile4", mobile4);
                    params.put("name5", name5);
                    params.put("mobile5", mobile5);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void clearFields() {
        n1.setText("");
        m1.setText("");
        n2.setText("");
        m2.setText("");
        n3.setText("");
        m3.setText("");
        n4.setText("");
        m4.setText("");
        n5.setText("");
        m5.setText("");
    }
}

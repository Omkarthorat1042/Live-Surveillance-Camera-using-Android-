package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Community extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, c1, c2, c3, c4, c5;
    EditText e1;
    Button b1;
    String user, password;
    String Name1, Name2, Name3, Name4, Name5;
    String Mobile1, Mobile2, Mobile3, Mobile4, Mobile5;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);

        e1 = findViewById(R.id.e1);
        b1 = findViewById(R.id.b1);

        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);
        c5 = findViewById(R.id.c5);

        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, 0);
        user = sharedPreferences.getString("user", "");
        password = sharedPreferences.getString("password", "");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent2 = new Intent(Community.this, AboutUs.class);
                        startActivity(intent2);
                        return true;
                    case R.id.menu_search:
                        Intent intent = new Intent(Community.this, Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_notifications:
                        Intent intent3 = new Intent(Community.this, RateUs.class);
                        startActivity(intent3);
                        return true;
                    case R.id.menu_profile:
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = e1.getText().toString().trim();
                if (msg.isEmpty()) {
                    msg = "Danger Alert..";
                }

                sendMessage(Mobile1, Name1, msg);
                sendMessage(Mobile2, Name2, msg);
                sendMessage(Mobile3, Name3, msg);
                sendMessage(Mobile4, Name4, msg);
                sendMessage(Mobile5, Name5, msg);

                if (Mobile1 == null && Mobile2 == null && Mobile3 == null && Mobile4 == null && Mobile5 == null) {
                    Toast.makeText(Community.this, "Add Community first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Community.this, "Message Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setContactClickListener(t1, Mobile1, Name1);
        setContactClickListener(t2, Mobile2, Name2);
        setContactClickListener(t3, Mobile3, Name3);
        setContactClickListener(t4, Mobile4, Name4);
        setContactClickListener(t5, Mobile5, Name5);

        getData();
    }

    private void setContactClickListener(TextView textView, final String mobile, final String name) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = e1.getText().toString().trim();
                if (msg.isEmpty()) {
                    msg = "Danger Alert..";
                }

                if (mobile == null || mobile.isEmpty()) {
                    Toast.makeText(Community.this, "No data", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(mobile, name, msg);
                    Toast.makeText(Community.this, "Message Sent to " + name, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String mobile, String name, String message) {
        if (mobile != null && !mobile.isEmpty()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobile, null, message, null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getData() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        AsyncTask<Void, Void, Void> getDataTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://tsm.ecssofttech.com/Ecom/api/RC_Get_Community.php?mobile=" + user)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseString = Objects.requireNonNull(response.body()).string();
                    JSONArray contacts = new JSONArray(responseString);

                    if (contacts.length() > 0) {
                        JSONObject c = contacts.getJSONObject(0);
                        Name1 = c.getString("Name1");
                        Name2 = c.getString("Name2");
                        Name3 = c.getString("Name3");
                        Name4 = c.getString("Name4");
                        Name5 = c.getString("Name5");

                        Mobile1 = c.getString("Mobile1");
                        Mobile2 = c.getString("Mobile2");
                        Mobile3 = c.getString("Mobile3");
                        Mobile4 = c.getString("Mobile4");
                        Mobile5 = c.getString("Mobile5");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                c1.setText(Name1);
                c2.setText(Name2);
                c3.setText(Name3);
                c4.setText(Name4);
                c5.setText(Name5);
            }
        };

        getDataTask.execute();
    }
}

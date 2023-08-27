package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutUs extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Handle home item selection

                        return true;
                    case R.id.menu_search:
                        // Handle search item selection

                        Intent intent = new Intent(AboutUs.this,Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_notifications:
                        // Handle notifications item selection
                        Intent intent2 = new Intent(AboutUs.this,RateUs.class);
                        startActivity(intent2);

                        return true;
                    case R.id.menu_profile:
                        // Handle profile item selection
                        Intent intent3 = new Intent(AboutUs.this,Community.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
    }
}
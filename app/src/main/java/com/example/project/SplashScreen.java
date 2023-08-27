package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SplashScreen extends AppCompatActivity {
    ImageView splash_imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //getSupportActionBar().hide();

        /*int[] yourListOfImages= { R.drawable.b2, R.drawable.b2,R.drawable.b2,R.drawable.b2};

        Random random = new Random(System.currentTimeMillis());
        int posOfImage = random.nextInt(yourListOfImages.length - 1);

        splash_imageview = findViewById(R.id.splash_imageview);
        splash_imageview.setBackgroundResource(yourListOfImages[posOfImage]);*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
                //boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn",false);
                String user = sharedPreferences.getString("user","");
                String password = sharedPreferences.getString("password","");

                if (user !="" & password !="")
                {
                    Intent intent = new Intent(SplashScreen.this, Home.class);
                    startActivity(intent);
                    finish();
                }

                else {
                    Intent intent = new Intent(SplashScreen.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}
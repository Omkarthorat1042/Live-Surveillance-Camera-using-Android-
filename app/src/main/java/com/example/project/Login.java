package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    VideoView videoView;
    EditText LUser, LPass;
    Button buttonL, buttonR;
    public static final String PREFS_NAME = "MyPrefsFile";

    ProgressBar progressBar, progressBar2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoView = findViewById(R.id.videoView);

        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        LUser = findViewById(R.id.LUser);
        LPass = findViewById(R.id.LPass);
        buttonL = findViewById(R.id.buttonL);
        buttonR = findViewById(R.id.buttonR);

        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = LUser.getText().toString().trim();
                String password = LPass.getText().toString().trim();

                if (mobile.isEmpty()) {
                    Toast.makeText(Login.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask().execute(mobile, password);
                }
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private String responseString;
        private String name;
        private String email;
        private String address;

        @Override
        protected void onPreExecute() {
            buttonL.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String mobile = params[0];
            String password = params[1];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://tsm.ecssofttech.com/Ecom/api/RC_Login.php?Mobile=" + mobile + "&Password=" + password)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseString = Objects.requireNonNull(response.body()).string();
                System.out.println(responseString);
                return responseString.equals("\tSuccess");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            buttonL.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (success) {
                Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", LUser.getText().toString());
                editor.putString("password", LPass.getText().toString());
                editor.apply();

                new GetDataTask().execute(LUser.getText().toString());

                // Start the Home activity
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Please Enter Correct Username and password", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class GetDataTask extends AsyncTask<String, Void, Boolean> {
        private String responseString;

        @Override
        protected void onPreExecute() {
            progressBar2.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String mobile = params[0];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://tsm.ecssofttech.com/Ecom/api/RC_Get_Profile.php?mobile=" + mobile)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseString = Objects.requireNonNull(response.body()).string();
                System.out.println(responseString);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressBar2.setVisibility(View.GONE);

            if (success) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    String name = jsonObject.getString("Name");
                    String email = jsonObject.getString("Email");
                    String address = jsonObject.getString("Address");

                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Name", name);
                    editor.putString("Email", email);
                    editor.putString("Address", address);
                    editor.apply();

                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Login.this, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        videoView.start();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        videoView.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }
}

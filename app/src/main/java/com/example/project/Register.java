package com.example.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText etName, etMobile, etEmail, etAddress, etPassword, etCPassword;
    VideoView videoView;
    Button Lbutton, Rbutton;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        Lbutton = findViewById(R.id.Lbutton);
        Rbutton = findViewById(R.id.Rbutton);

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Please Wait");

        final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[0-9])" +         //at least 1 digit
                        "(?=.*[a-z])" +         //at least 1 lower case letter
                        "(?=.*[A-Z])" +         //at least 1 upper case letter
                        // "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$");

        Lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        Rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rbutton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                String Name = etName.getText().toString().trim();
                String Mobile = etMobile.getText().toString().trim();
                String Email = etEmail.getText().toString().trim();
                String Address = etAddress.getText().toString().trim();
                String Password = etPassword.getText().toString().trim();
                String ConfirmPassword = etCPassword.getText().toString().trim();

                if (Name.isEmpty()) {
                    showToast("Enter Name");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (Mobile.isEmpty()) {
                    showToast("Enter Mobile Number");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (Mobile.length() != 10) {
                    showToast("Mobile Number must have a length of 10 digits");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!Pattern.matches("^[6-9]\\d{9}$", Mobile)) {
                    showToast("Invalid Mobile Number");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (Email.isEmpty()) {
                    showToast("Enter Email Address");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (Address.isEmpty()) {
                    showToast("Enter Address");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (Password.isEmpty()) {
                    showToast("Enter Password");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (ConfirmPassword.isEmpty()) {
                    showToast("Enter Confirm Password");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!PASSWORD_PATTERN.matcher(Password).matches()) {
                    showToast("Weak Password");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!Password.equals(ConfirmPassword)) {
                    showToast("Password Does Not Match");
                    Rbutton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                progressDialog.show(); // Show the progress dialog

                StringRequest request = new StringRequest(Request.Method.POST, "http://tsm.ecssofttech.com/Ecom/api/RC_Registration.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String s1 = "Record Inserted Successfully";
                        String s2 = "User Already Exist";

                        if (response.equalsIgnoreCase(s2)) {
                            showToast("User Already Exist");
                        } else if (response.equalsIgnoreCase(s1)) {
                            showToast("Register Successfully");
                            SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", etMobile.getText().toString());
                            editor.putString("password", etPassword.getText().toString());
                            editor.apply();
                            editor.commit();

                            Intent intent = new Intent(Register.this, Home.class);
                            startActivity(intent);
                            finish();
                        }

                        progressDialog.dismiss(); // Dismiss the progress dialog
                        resetFields();
                        Rbutton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Failed");
                        progressDialog.dismiss(); // Dismiss the progress dialog
                        Rbutton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Name", Name);
                        params.put("Mobile", Mobile);
                        params.put("Email", Email);
                        params.put("Address", Address);
                        params.put("Password", Password);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                requestQueue.add(request);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetFields() {
        etName.setText("");
        etAddress.setText("");
        etMobile.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etCPassword.setText("");
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent in2 = new Intent(Register.this, Login.class);
        startActivity(in2);
        finish();
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

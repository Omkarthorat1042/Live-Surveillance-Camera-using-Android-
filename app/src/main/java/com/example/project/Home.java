package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView nav;
    public Toolbar toolbar;



    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =101 ;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    Button btnGenerate,btnPre;
    ImageView imageCode;
    String QRCode;
    static TextView t1;
    public String Ip;
    String Value;
    String user,password;
    private String buttonText;

    private BottomNavigationView bottomNavigationView;
    LottieAnimationView animationView;
    TextView textView8;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav = findViewById(R.id.nav);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {

                    case R.id.menu_Home:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_Community:
                        Intent intent3 = new Intent(Home.this, AddCommunity.class);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.menu_Scan:
                        Intent intent2 = new Intent(Home.this, Scan.class);
                        startActivity(intent2);
                        finish();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_Logout:
                        SharedPreferences.Editor editor = getSharedPreferences(Login.PREFS_NAME,0).edit();
                        editor.remove("user");
                        editor.remove("password");
                        editor.apply();
                        overridePendingTransition(0,0);
                        finish();
                        Toast.makeText(Home.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Home.this, Login.class);
                        startActivity(intent1);
                        finish();
                        //drawerLayout.closeDrawer(GravityCompat.START);
                        break;




                    default:
                        return true;

                }
                return true;
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        textView8 = findViewById(R.id.textView8);
        // Set the item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Handle home item selection
                        Intent intent2 = new Intent(Home.this,AboutUs.class);
                        startActivity(intent2);

                        return true;
                    case R.id.menu_search:
                        // Handle search item selection

                        Intent intent = new Intent(Home.this,SourceMainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_notifications:
                        // Handle notifications item selection
                        Intent intent1 = new Intent(Home.this,RateUs.class);
                        startActivity(intent1);

                        return true;
                    case R.id.menu_profile:
                        // Handle profile item selection
                        Intent intent3 = new Intent(Home.this,Community.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        t1 = findViewById(R.id.t1);
        btnPre = findViewById(R.id.btnPre);
        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
        //boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn",false);
        user = sharedPreferences.getString("user","");
        password = sharedPreferences.getString("password","");

        animationView = findViewById(R.id.animation_view);

        String Name = sharedPreferences.getString("Name", "");
        String Email = sharedPreferences.getString("Email", "");
        String Address = sharedPreferences.getString("Address", "");
        NavigationView navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_name = (TextView) headerView.findViewById(R.id.nav_name);
        TextView nav_mobile = (TextView) headerView.findViewById(R.id.nav_mobile);
        TextView nav_email = (TextView) headerView.findViewById(R.id.nav_email);
        TextView nav_address = (TextView) headerView.findViewById(R.id.nav_address);
        nav_name.setText(Name);
        nav_mobile.setText(user);
        nav_email.setText(Email);
        nav_address.setText(Address);

        //QRCode = user+password;

        // Check if the app has permission to write to external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If the app does not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

// Check if the app has permission to use the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // If the app does not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        getIPAddress(true);

        Ip = t1.getText().toString().trim();

        String qr = Ip+","+user;

        Handler ha=new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {



                    getValue();

                    if (Value.equals("1"))
                    {

                        Intent intent = new Intent(Home.this,SourceMainActivity.class);
                        startActivity(intent);
                        finish();

                        finshData();

                    }
                    else {
                        //  Toast.makeText(Location.this, "No...", Toast.LENGTH_SHORT).show();
                    }
                ha.postDelayed(this, 1000);
            }
        }, 1000);

        //Button for generating QR code
        btnGenerate = findViewById(R.id.btnGenerate);
        //Text will be entered here to generate QR code
        //ImageView for generated QR code
        imageCode = findViewById(R.id.imageCode);
        buttonText = "Generate QR Code";
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonText.equals("Generate QR Code")) {
                    // Generate QR code and display it
                    // Initializing MultiFormatWriter for QR code
                    MultiFormatWriter mWriter = new MultiFormatWriter();
                    try {
                        // BitMatrix class to encode entered text and set Width & Height
                        BitMatrix mMatrix = mWriter.encode(qr, BarcodeFormat.QR_CODE, 1000, 1000);
                        BarcodeEncoder mEncoder = new BarcodeEncoder();
                        Bitmap mBitmap = mEncoder.createBitmap(mMatrix); // Creating bitmap of code
                        imageCode.setImageBitmap(mBitmap); // Setting generated QR code to imageView

                        // Update button text and hide QR code
                        buttonText = "Hide QR Code";
                        imageCode.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.GONE);
                        textView8.setText("Scan QR Code For Connect Device");
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Hide QR code and restore button text
                    buttonText = "Generate QR Code";
                    imageCode.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);
                    textView8.setText("Generate QR Code For Connect Device");
                }

                btnGenerate.setText(buttonText);
            }
        });


        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,SourceMainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void finshData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Request.Builder()
                .url("http://tsm.ecssofttech.com/Ecom/api/RC_Value_Update3.php?mobile="+user+"")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseString = Objects.requireNonNull(response.body()).string();
            System.out.println(responseString);
            String str = "Record updated successfully";
            if (responseString.equals(str)){

                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            }

            else {
                Toast.makeText(Home.this,"Scan Again",Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getValue() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://tsm.ecssofttech.com/Ecom/api/RC_Get_Value1.php?mobile="+user+"")
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            String responseString = Objects.requireNonNull(response.body()).string();
            System.out.println(responseString);

            JSONArray contacts = new JSONArray(responseString);


            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                Value = c.getString("Value");

            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the grantResults array is empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, write to external storage
                    // Write your code to perform the operation that requires the permission
                } else {
                    // Permission denied, show alert dialog
                    // Show an alert dialog to inform the user that the app needs this permission to function properly
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the grantResults array is empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, use the camera
                    // Write your code to perform the operation that requires the permission
                } else {
                    // Permission denied, show alert dialog
                    // Show an alert dialog to inform the user that the app needs this permission to function properly
                }
                return;
            }
        }
    }
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        t1.setText(sAddr);
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent in2 = new Intent(Home.this, Login.class);
        startActivity(in2);
        finish();
    }
}
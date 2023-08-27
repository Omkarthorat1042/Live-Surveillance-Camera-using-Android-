package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Scan extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView nav;
    public Toolbar toolbar;



    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =101 ;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    String user,password;
    Button btnScan,btnSee;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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
                    case R.id.menu_Community:
                        Intent intent3 = new Intent(Scan.this, AddCommunity.class);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.menu_Scan:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_Home:
                        Intent intent2 = new Intent(Scan.this, Home.class);
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
                        Toast.makeText(Scan.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Scan.this, Login.class);
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

        // Set the item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Handle home item selection
                        Intent intent2 = new Intent(Scan.this,AboutUs.class);
                        startActivity(intent2);
                        return true;
                    case R.id.menu_search:
                        // Handle search item selection

                        Intent intent = new Intent(Scan.this,ClientMainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_notifications:
                        // Handle notifications item selection
                        Intent intent1 = new Intent(Scan.this,RateUs.class);
                        startActivity(intent1);
                        return true;
                    case R.id.menu_profile:
                        // Handle profile item selection
                        Intent intent3 = new Intent(Scan.this,Community.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
        user = sharedPreferences.getString("user","");
        password = sharedPreferences.getString("password","");
        btnScan = findViewById(R.id.btnScan);
        btnSee = findViewById(R.id.btnSee);


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

        btnScan.setOnClickListener(v->
        {
            SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.remove("SERVERIP");
            editor.apply();

            scanCode();
        });

        btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Scan.this,ClientMainActivity.class);
                startActivity(intent);
            }
        });

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


    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
        btnScan.setText("Cancel");

        /*SharedPreferences.Editor editor = getSharedPreferences(ClientMainActivity.IP_ADDRESS_KEY, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();*/
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(Scan.this);
            builder.setTitle("QR Scan");
            builder.setMessage("QR Scan Sucessfully");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                    String Data = result.getContents();

                     String[] result = Data.split(",");

                     String Ip1 = result[0];
                     String user = result[1];


                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Ecom/api/RC_Value_Update2.php?mobile="+user+"")
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        String responseString = Objects.requireNonNull(response.body()).string();
                        System.out.println(responseString);
                        String str = "Record updated successfully";
                        if (responseString.equals(str)){

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(Scan.this,ClientMainActivity.class);
                                    intent.putExtra("Ip",Ip1);
                                    startActivity(intent);

                                    // insertData();
                                    btnScan.setText("Scan QR");
                                    dialogInterface.dismiss();

                                }
                            },2000);

                        }

                        else {
                            Toast.makeText(Scan.this,"Scan Again",Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(),"You Cancel QR Scan",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    btnScan.setText("Scan QR");
                }
            }).show();
        }
        else {
            btnScan.setText("Scan QR");
        }
    });

    public void onBackPressed() {
        super.onBackPressed();
        Intent in2 = new Intent(Scan.this, Login.class);
        startActivity(in2);
        finish();
    }
}
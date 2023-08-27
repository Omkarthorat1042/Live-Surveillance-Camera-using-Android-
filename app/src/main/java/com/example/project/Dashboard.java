package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
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

public class Dashboard extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =101 ;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    Button btnGenerate,btnScan,btnSee,btnPre;
    ImageView imageCode;
    String QRCode;
    static TextView t1;
    public String Ip;
    String Value;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        t1 = findViewById(R.id.t1);
        btnScan = findViewById(R.id.btnScan);
        btnSee = findViewById(R.id.btnSee);
        btnPre = findViewById(R.id.btnPre);
        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
        //boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn",false);
        String user = sharedPreferences.getString("user","");
        String password = sharedPreferences.getString("password","");

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


        /*GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null){
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
        }*/

        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });*/

        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(Login.PREFS_NAME,0).edit();
                editor.remove("user");
                editor.remove("password");
                editor.apply();
                overridePendingTransition(0,0);
                finish();
                Toast.makeText(Dashboard.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(Dashboard.this, Login.class);
                startActivity(intent1);
                finish();
            }
        });*/


        /*Handler ha=new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {



                    getValue();

                    if (Value.equals("1"))
                    {

                        Intent intent = new Intent(Dashboard.this,SourceMainActivity.class);
                        startActivity(intent);
                        finish();

                        finshData();

                    }
                    else {
                        //  Toast.makeText(Location.this, "No...", Toast.LENGTH_SHORT).show();
                    }
                ha.postDelayed(this, 5000);
            }
        }, 5000);*/

        //Button for generating QR code
        btnGenerate = findViewById(R.id.btnGenerate);
        //Text will be entered here to generate QR code
        //ImageView for generated QR code
        imageCode = findViewById(R.id.imageCode);
        btnGenerate.setOnClickListener(v -> {
            //initializing MultiFormatWriter for QR code
            MultiFormatWriter mWriter = new MultiFormatWriter();
            try {
                //BitMatrix class to encode entered text and set Width & Height
                BitMatrix mMatrix = mWriter.encode(Ip, BarcodeFormat.QR_CODE, 500,500);
                BarcodeEncoder mEncoder = new BarcodeEncoder();
                Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
                imageCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView
                // to hide the keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        btnScan.setOnClickListener(v->
        {
            scanCode();
        });

        btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,ClientMainActivity.class);
                startActivity(intent);
            }
        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,SourceMainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void finshData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new Request.Builder()
                .url("http://tsm.ecssofttech.com/Ecom/api/RC_Value_Update1.php")
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
                Toast.makeText(Dashboard.this,"Scan Again",Toast.LENGTH_SHORT).show();
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
                .url("http://tsm.ecssofttech.com/Ecom/api/RC_Get_Value.php")
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

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
        btnScan.setText("Cancel");
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            builder.setTitle("QR Scan");
            builder.setMessage("QR Scan Sucessfully");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                    String Data = result.getContents();

                   // String[] result = Data.split(",");

                   // String Ip = result[0];
                   // String user = result[1];

                    /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Ecom/api/RC_Value_Update2.php?Mobile="+user+"")
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        String responseString = Objects.requireNonNull(response.body()).string();
                        System.out.println(responseString);
                        String str = "Record updated successfully";
                        if (responseString.equals(str)){

                            Intent intent = new Intent(Dashboard.this,ClientMainActivity.class);
                            intent.putExtra("Ip",Data);
                            startActivity(intent);

                        }

                        else {
                            Toast.makeText(Dashboard.this,"Scan Again",Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    Intent intent = new Intent(Dashboard.this,ClientMainActivity.class);
                    intent.putExtra("Ip",Data);
                    startActivity(intent);

                   // insertData();
                    btnScan.setText("Scan QR");
                    dialogInterface.dismiss();

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

}
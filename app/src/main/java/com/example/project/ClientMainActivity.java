package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMainActivity extends AppCompatActivity {
    private TextView mStatus;
    private ImageView mCameraView;
    private Button pauseButton,topRightButton;

    public static final int SERVERPORT = 9191;
    public ClientMyClientThread mClient;
    public Bitmap mLastFrame;

    private int face_count;
    private final Handler handler = new ClientMyHandler(this);
    private boolean isPaused = false;

    private FaceDetector mFaceDetector = new FaceDetector(320, 240, 10);
    private FaceDetector.Face[] faces = new FaceDetector.Face[10];
    private PointF tmp_point = new PointF();
    private Paint tmp_paint = new Paint();

    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (!isPaused) {
                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mLastFrame != null) {
                                Bitmap mutableBitmap = mLastFrame.copy(Bitmap.Config.RGB_565, true);
                                face_count = mFaceDetector.findFaces(mLastFrame, faces);
                                Log.d("Face_Detection", "Face Count: " + String.valueOf(face_count));
                                Canvas canvas = new Canvas(mutableBitmap);

                                for (int i = 0; i < face_count; i++) {
                                    FaceDetector.Face face = faces[i];
                                    tmp_paint.setColor(Color.RED);
                                    tmp_paint.setAlpha(100);
                                    face.getMidPoint(tmp_point);
                                    canvas.drawCircle(tmp_point.x, tmp_point.y, face.eyesDistance(),
                                            tmp_paint);
                                }

                                mCameraView.setImageBitmap(mutableBitmap);
                            }
                        }
                    });
                } finally {
                    handler.postDelayed(mStatusChecker, 1000 / 15);
                }
            } else {
                handler.postDelayed(mStatusChecker, 1000 / 15);
            }
        }
    };

    RelativeLayout relative;

    @SuppressLint({"StaticFieldLeak", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity_main);

        mCameraView = findViewById(R.id.camera_preview);
        mStatus = findViewById(R.id.textView);
        pauseButton = findViewById(R.id.pauseButton);
        relative = findViewById(R.id.relative);
        topRightButton = findViewById(R.id.topRightButton);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String storedIp = sharedPreferences.getString("SERVERIP", null);

        Intent intent = getIntent();
        final String serverIp = intent.getStringExtra("Ip");

        if (serverIp != null) {
            // Update the stored IP if it's not null
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SERVERIP", serverIp);
            editor.apply();
        }

        final String finalServerIp = (serverIp != null) ? serverIp : storedIp;
        mStatus.setText(finalServerIp);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unused) {
                // Background Code
                Socket s;
                try {
                    s = new Socket(finalServerIp, SERVERPORT);
                    mClient = new ClientMyClientThread(s, handler);
                    new Thread(mClient).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePause();
            }
        });

        topRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
        mStatusChecker.run();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("Resume");
            relative.setBackgroundColor(Color.RED);
        } else {
            pauseButton.setText("Pause");
            relative.setBackgroundColor(Color.GREEN);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        if (source != null) {
            Bitmap retVal;

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            source.recycle();
            return retVal;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in2 = new Intent(ClientMainActivity.this, Scan.class);
        startActivity(in2);
        finish();
    }




    private void sendData() {
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://tsm.ecssofttech.com/Library/api/RC_Send_Siren.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s1 = "Record Inserted Successfully";

                if(response.equalsIgnoreCase(s1)) {
                    Toast.makeText(ClientMainActivity.this, "Aleart", Toast.LENGTH_SHORT).show();

                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ClientMainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Value","1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ClientMainActivity.this);
        requestQueue.add(request);
    }
}

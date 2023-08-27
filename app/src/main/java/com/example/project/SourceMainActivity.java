package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SourceMainActivity extends AppCompatActivity {
    private Camera mCamera;
    public SourceMyCameraView mPreview;
    public TextView serverStatus;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    public static String SERVERIP = "localhost";
    public static final int SERVERPORT = 9191;
    private Handler handler = new Handler();

    Button captureButton,stopButton;
    TextView recordingStatus; // TextView to show recording status
    boolean isRecording = false; // Flag to indicate if recording is in progress
    MediaRecorder mediaRecorder;
    private File videoFile; // Declare videoFile as a class member
    private String videoFileName;

    RelativeLayout relative;

    private boolean motionDetected = false;
    private static final int MOTION_THRESHOLD = 100; // Adjust this threshold value as per your requirements


    String Flag;
    public MediaPlayer mediaPlayer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_activity_main);
        serverStatus = findViewById(R.id.textView);
        recordingStatus = findViewById(R.id.recordingStatus); // Initialize the recording status TextView
        SERVERIP = getLocalIpAddress();
        mCamera = getCameraInstance();
        mPreview = new SourceMyCameraView(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        relative = findViewById(R.id.relative);

        captureButton = findViewById(R.id.captureButton);
        stopButton = findViewById(R.id.stopButton); // Find the stopButton view


        Handler ha=new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {


                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Library/api/RC_Start_Siren.php")
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        String responseString = Objects.requireNonNull(response.body()).string();
                        System.out.println(responseString);

                        JSONArray contacts = new JSONArray(responseString);

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);

                            Flag = c.getString("Value").toString().trim();
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    if (Flag.equals("1"))
                    {
                        mediaPlayer = null;
                        if(mediaPlayer == null){
                            mediaPlayer = MediaPlayer.create(SourceMainActivity.this, R.raw.danger);
                        }
                        // Then, register OnCompletionListener that calls a user supplied callback method onCompletion() when
                        // looping mode was set to false to indicate playback is completed.
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                // Here, call a method to release the MediaPlayer object and to set it to null.
                                stopMusic();
                                finshData();
                            }
                        });
                        // Next, call start() method on mediaPlayer to start playing the music.
                        mediaPlayer.start();
                    }

                ha.postDelayed(this, 5000);
            }
        }, 100);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCaptureButtonClick(v);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() { // Set the click listener for the stopButton
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        //startMotionDetection();

        Thread cThread = new Thread(new SourceMyServerThread(this, SERVERIP, SERVERPORT, handler));
        cThread.start();

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                int motion = calculateMotion(data, camera.getParameters());
                if (motion > MOTION_THRESHOLD && !motionDetected) {
                    motionDetected = true;
                    Toast.makeText(SourceMainActivity.this, "Motion detected", Toast.LENGTH_SHORT).show();
                    activateSiren();
                } else if (motion <= MOTION_THRESHOLD && motionDetected) {
                    motionDetected = false;
                }
            }
        });

    }

    /**
     * Get local IP address of the phone
     *
     * @return ipAddress
     */
    private String getLocalIpAddress() {
        try {
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                java.net.NetworkInterface intf = en.nextElement();
                for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (java.net.SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    /**
     * Get camera instance
     *
     * @return Camera instance
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void onCaptureButtonClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        } else {
            recordAndUploadVideo();
            //startMotionDetection();
        }
    }


    private void recordAndUploadVideo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentTimeStamp = dateFormat.format(new Date());
        videoFileName = "video_" + currentTimeStamp + ".mp4";
        videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), videoFileName);

        // Start recording video
            mCamera.unlock();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());



        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            // Update the recording status flag and UI
            isRecording = true;
            recordingStatus.setText("Recording...");
            recordingStatus.setTextColor(Color.RED);
            relative.setBackgroundColor(Color.RED);

            // You can add a stop button or other user interaction to stop the recording
            // For example, you can add a stop button and call stopRecording() method on its click event
             stopButton.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                     stopRecording();
                 }
             });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordingStatus.setText(""); // Clear the recording status UI

            // Upload the video to FTP server
            uploadVideoToFTP(videoFile, "/Ecom/" + videoFileName);

            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mCamera.lock();
        }
    }
    private void uploadVideoToFTP(File videoFile, String remoteFilePath) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;

        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.connect("182.50.132.54", 21);
            ftpClient.login("ph123", "Rahulecs@123");

            fis = new FileInputStream(videoFile);
            boolean uploadSuccess = ftpClient.storeFile(remoteFilePath, fis);

            if (uploadSuccess) {
                Log.d("Upload", "Video uploaded successfully to FTP server.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordingStatus.setText("Upload finished");
                        recordingStatus.setTextColor(Color.GREEN);
                        relative.setBackgroundColor(Color.GREEN);
                    }
                });
            } else {
                Log.e("Upload", "Failed to upload video to FTP server.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordingStatus.setText("Upload failed");
                        recordingStatus.setTextColor(Color.RED);
                        relative.setBackgroundColor(Color.RED);
                    }
                });
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Upload", "Failed to upload video to FTP server.");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordingStatus.setText("Upload failed");
                    recordingStatus.setTextColor(Color.RED);
                    relative.setBackgroundColor(Color.RED);
                }
            });
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                recordAndUploadVideo();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRecording) {
            isRecording = false;
            recordingStatus.setText(""); // Clear the recording status UI
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mCamera.lock();
        }

        if (videoFile != null) {
            // Upload the video to FTP server
            uploadVideoToFTP(videoFile, "/Ecom/" + videoFileName);
        }

        Intent in2 = new Intent(SourceMainActivity.this, Home.class);
        startActivity(in2);
        finish();
    }










    private void startMotionDetection() {
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                int motion = calculateMotion(data, camera.getParameters());
                if (motion > MOTION_THRESHOLD && !motionDetected) {
                    motionDetected = true;
                    Toast.makeText(SourceMainActivity.this, "Motion detected", Toast.LENGTH_SHORT).show();
                    activateSiren();
                } else if (motion <= MOTION_THRESHOLD && motionDetected) {
                    motionDetected = false;
                }
            }
        });
    }

    private int calculateMotion(byte[] data, Camera.Parameters parameters) {
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        int frameSize = width * height;
        int[] pixels = new int[frameSize];
        int sum = 0;

        // Convert the byte data to pixel values
        for (int i = 0; i < frameSize; ++i) {
            int y = (0xff & ((int) data[i])) - 16;
            int v = (0xff & ((int) data[frameSize + i])) - 128;
            int u = (0xff & ((int) data[frameSize + i + frameSize / 4])) - 128;

            int r = (int) (1.164 * y + 1.596 * v);
            int g = (int) (1.164 * y - 0.813 * v - 0.391 * u);
            int b = (int) (1.164 * y + 2.018 * u);

            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            pixels[i] = Color.rgb(r, g, b);
        }

        // Calculate the motion intensity
        for (int i = 0; i < frameSize; i++) {
            int prevPixel = pixels[i];
            int curPixel = pixels[i + 1];
            int diff = Math.abs(prevPixel - curPixel);
            sum += (diff >> 16) & 0xFF; // Red component
            sum += (diff >> 8) & 0xFF; // Green component
            sum += diff & 0xFF; // Blue component
        }

        return sum / frameSize;
    }

    private void activateSiren() {
        // Add code to activate the siren or any other action here
        Toast.makeText(SourceMainActivity.this, "Motion detected", Toast.LENGTH_SHORT).show();
    }

    private void stopMusic() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void finshData() {
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://tsm.ecssofttech.com/Library/api/RC_Stop_Siren.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s1 = "Record Inserted Successfully";

                if(response.equalsIgnoreCase(s1)) {
                    Toast.makeText(SourceMainActivity.this, "Aleart", Toast.LENGTH_SHORT).show();

                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SourceMainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Value","0");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SourceMainActivity.this);
        requestQueue.add(request);
    }

}

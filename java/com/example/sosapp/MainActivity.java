package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public SwitchCompat s;
    public gyroscope gyro;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    ListView contactList;
    FusedLocationProviderClient fusedLocationProviderClient;
    public String userLocation;
    public int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 100);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},100);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        showContacts();

        startService(new Intent(getApplicationContext(), LockService.class));

        s = findViewById(R.id.switch2);
        gyro = new gyroscope(this);
        s.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                startService();
            } else {
                stopService();
            }
        });
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        checkShake();
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }

    public void showContacts() {
        contactList = findViewById(R.id.sosContacts);
        list = new ArrayList<>();
        list.add("Contact 1");
        list.add("Contact 2");
        list.add("Contact 3");
        adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        contactList.setAdapter(adapter);

    }

 //   public void sendSMS() {
//
//        try {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+1-555-521-5554", null, "Iam in danger, Help me! My location: " + userLocation, null, null);
//            Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
//            Log.i("emergency", "message sent");
//        } catch (Exception e) {
//            Log.i("fault", String.valueOf(e));
//        }
//    }
//        else
//
//    {
//        Log.i("check", "build condition true");
//        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
//    }
    // flag=0;
//}

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = "http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+1-555-521-5554", null, "Iam in danger, Help me! My location: " + userLocation, null, null);
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                Log.i("loc", userLocation);
            } else
                Log.i("loc", "null loc");
        });

    }

    public void askPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyro.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyro.unregister();
    }

    public void checkShake() {
        gyro.setListener((x, y, z) -> {
            if (flag == 0) {
                if (z > 1.0f || z < -1.0f || y > 1.0f || y < -1.0f || x > 1.0f || x < -1.0f) {
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    //flag = 1;
                    gyro.unregister();
                    getLastLocation();

                }
            }
        });
    }

}

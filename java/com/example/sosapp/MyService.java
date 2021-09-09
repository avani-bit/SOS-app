package com.example.sosapp;

import static com.example.sosapp.notification.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SOS app enabled", Toast.LENGTH_SHORT).show();
        Log.i("Service", "service started");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOS App Service")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "SOS app disabled", Toast.LENGTH_SHORT).show();
        Log.i("Service", "service stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}
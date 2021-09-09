package com.example.sosapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    public int count =0;
    public boolean wasScreenOn = true;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("LOB","onReceive");
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                count++;
                wasScreenOn = false;
                Log.e("LOB", "wasScreenOn" + wasScreenOn);
                break;
            case Intent.ACTION_SCREEN_ON:
                count++;
                wasScreenOn = true;

                break;
            case Intent.ACTION_USER_PRESENT:
                Log.e("LOB", "userpresent");
                Log.e("LOB", "wasScreenOn" + wasScreenOn);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
        }

        if(count==8) {
            Log.e("info", "pressed 4 times");
            //getLastLocation();
        }
    }
}


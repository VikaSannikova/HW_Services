package com.example.hw_services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
        public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("___", "im in reciever");

        Intent serviceIntent = new Intent(context, MyService.class);
        serviceIntent.putExtra("status", "stop");
        serviceIntent.setAction("CANCEL_ACTION");
        context.startService(serviceIntent);
        //ContextCompat.startForegroundService(context, serviceIntent);
    }
}
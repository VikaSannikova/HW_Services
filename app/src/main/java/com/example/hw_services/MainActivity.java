package com.example.hw_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button first_button, second_button,
            bind_service, unbind_service, get_count_service,
            start_button, stop_button,
            startforeground_button, stopforeground_button;
    TextView get_count_tv;
    int count_plant = 0;
    int count_cat = 0;

    // Идентификатор уведомления
    private static final int NOTIFY_ID1 = 101;
    private static final int NOTIFY_ID2 = 102;
    public static final int NOTIFY_ID3 = 103;

    // Идентификатор канала
    private static String CHANNEL_ID1 = "Plant channel";
    private static String CHANNEL_ID2 = "Cat channel";
    public static String CHANNEL_ID3 = "Foreground channel";

    private MyService myService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("___","SERVICE CONNECTED");
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("___","SERVICE DISCONNECTED");
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        first_button = (Button) findViewById(R.id.button1);
        second_button = (Button) findViewById(R.id.button2);
        bind_service = (Button) findViewById(R.id.start_service);
        unbind_service = (Button) findViewById(R.id.stop_service);
        get_count_service = (Button) findViewById(R.id.get_count_service);
        get_count_tv = (TextView) findViewById(R.id.get_count_textview);
        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button)findViewById(R.id.stop_button);
        startforeground_button = (Button) findViewById(R.id.startforeground);
        stopforeground_button = (Button)findViewById(R.id.stopforegrpund);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID1,
                    "CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID2,
                    "CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel3 = new NotificationChannel(CHANNEL_ID3,
                    "CHANNEL FOREGROUND",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }

        first_button.setOnClickListener(v -> {
            Log.d("___", "plant");
            count_plant++;
//            NotificationCompat.Builder builder =
//                    new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID1)
//                            .setSmallIcon(R.drawable.plant)
//                            .setContentTitle("Напоминание")
//                            .setContentText("Пора полить цветы " + count_plant)
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            .setAutoCancel(true);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID1);
            RemoteViews view = new RemoteViews(getPackageName(),R.layout.notification);
            Intent intent = new Intent(this, DetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, 0);
            view.setOnClickPendingIntent(R.id.button111, pendingIntent);
            builder.setSmallIcon(R.drawable.plant);
            builder.setContent(view);



            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(NOTIFY_ID2, builder.build());
        });

        second_button.setOnClickListener(v -> {
            Log.d("___", "cat");
            count_cat++;
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID2)
                            .setSmallIcon(R.drawable.cat)
                            .setContentTitle("Напоминание")
                            .setContentText("Пора покормить кота " + count_cat)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(NOTIFY_ID1, builder.build());
        });

        bind_service.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        });

        unbind_service.setOnClickListener(v -> {
            if(mBound) {
                mBound = false;
                unbindService(mConnection);
                get_count_tv.setText("0");
            }
        });

        get_count_service.setOnClickListener(v -> {
            if (mBound){
                get_count_tv.setText(String.valueOf(myService.getCount()));
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);
            }
        });
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                stopService(intent);
            }
        });

        startforeground_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("___", "foreground click");
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("inputExtra", "Foreground Service started !!!");
                serviceIntent.putExtra("status", "start");
                serviceIntent.setAction("ACTION_START");
                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

            }
        });

        stopforeground_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                stopService(serviceIntent);
            }
        });

    }
}
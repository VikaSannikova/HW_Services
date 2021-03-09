package com.example.hw_services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.hw_services.MainActivity.CHANNEL_ID3;
import static com.example.hw_services.MainActivity.NOTIFY_ID3;

public class MyService extends Service {
    private final IBinder binder = new LocalBinder();
    private int count = 0;
    public Thread thread_for_job;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("___", "BIND SERVICE");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("___", "DESTROY SERVICE " + this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("___", "UNBIND SERVICE");
        return super.onUnbind(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID3,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("___", "START SERVICE " + this);
        /*
         * Foreground block
         */
        if (intent.getStringExtra("status").equals("start")) {
            String input = intent.getStringExtra("inputExtra");
            createNotificationChannel();
            Intent myintent = new Intent(this, MyReceiver.class);
            PendingIntent pendingIntentforButton = PendingIntent.getBroadcast(this, 0, myintent, 0);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID3);
            builder.setContentTitle("Foreground Service")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.cat)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Остановить загрузку",
                            pendingIntentforButton);
            notificationManager.notify(NOTIFY_ID3, builder.build());
            startForeground(NOTIFY_ID3, builder.build());

            thread_for_job = new Thread( new Runnable() {
                @SuppressLint("RestrictedApi")
                @Override
                public void run() {
                    Log.d("___", "thread " + thread_for_job);
                    int incr;
                    for (incr = 0; incr <= 100; incr+=5) {
                        if(!thread_for_job.isInterrupted()){
                            Log.d("___", "do job " + Thread.currentThread());
                            builder.setProgress(100, incr, false);
                            notificationManager.notify(103, builder.build());
                            try {
                                Thread.sleep(1*500);
                            } catch (InterruptedException e) {
                                Log.d("TAG", this + " sleep failure " + Thread.currentThread());
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            Log.d("___", "stop loop");
                            break;
                        }
                    }
                    Log.d("___", String.valueOf(thread_for_job.isInterrupted()));
                    if(!thread_for_job.isInterrupted()) {
                        builder.setContentText("Download completed")
                                .setProgress(0,0,false);
                    } else {
                        builder.setContentText("CANCELED")
                               .setProgress(0,0,false);
                        builder.mActions.clear();
                    }
                    notificationManager.notify(NOTIFY_ID3, builder.build());
                    stopForeground(false);
                }
            });
            thread_for_job.start();

        } else if (intent.getStringExtra("status").equals("stop")) {
            thread_for_job.interrupt();
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if(!intent.getAction().equals("CANCEL_ACTION")) {
//            Log.d("___", "START FOREGROUND SERVICE");
//
//            createNotificationChannel();
//
//            Intent cancelActionIntent = new Intent(this,MyReceiver.class);
//            PendingIntent pendingReceiverIntent = PendingIntent.getBroadcast(this, 0, cancelActionIntent, 0);
//            Intent notificationIntent = new Intent(this,MainActivity.class);
//            PendingIntent pendingActivityIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID3)
//                    .setSmallIcon(R.drawable.cat)
//                    .setContentTitle("Download")
//                    .setContentText("Download in progress")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setContentIntent(pendingActivityIntent)
//                    .addAction(R.drawable.ic_launcher_background, "Cancel",pendingReceiverIntent)
//                    .setOngoing(false)
//                    .setProgress(100, 0, false);
//
//            notificationManager.notify(NOTIFY_ID3, notification.build());
//
//            startForeground(NOTIFY_ID3,notification.build());
//
//            thread_for_job = new Thread (new Runnable() {
//                @Override
//                public void run() {
//                    for (int progress = 0; progress <= 100; progress += 10) {
//                        if (!thread_for_job.isInterrupted()) {
//                            notification.setProgress(100, progress, false);
//                            notificationManager.notify(NOTIFY_ID3, notification.build());
//
//                            SystemClock.sleep(1000);
//                        }
//                        else {
//                            break;
//                        }
//                    }
//                    if (!thread_for_job.isInterrupted()) {
//                        notification.setContentText("Download finished")
//                                .setProgress(0, 0, false)
//                                .setOngoing(false);
//                    }
//                    else
//                    {
//                        notification.setContentText("Download Canceled!");
//                    }
//                    notificationManager.notify(NOTIFY_ID3, notification.build());
//                }
//            });
//            thread_for_job.start();
//        }
//        if(intent.getAction() != null && intent.getAction().equals("CANCEL_ACTION")) {
//            thread_for_job.interrupt();
//            stopForeground(true);
//            stopSelf();
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }


    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    public int getSum( int a, int b) {
        return a+b;
    }

    public int getCount() {
        Log.d("___", "GET COUNT");
        return ++count;
    }
}
package com.example.hw_services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final IBinder binder = new LocalBinder();
    private int count = 0;

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
        Log.d("___", "DESTROY SERVICE");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("___", "UNBIND SERVICE");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("___", "START SERVICE");
        return super.onStartCommand(intent, flags, startId);
    }

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
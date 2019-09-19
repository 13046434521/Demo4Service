package com.jtl.demo4service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.socks.library.KLog;

import androidx.annotation.Nullable;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/19 19:50
 * 描述: Service 示范类
 * 更改:
 */
public class DemoService extends Service {
    private static final String TAG=DemoService.class.getSimpleName();
    private MyBinder mMyBinder=new MyBinder();
    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d(TAG,"onBind");
        return mMyBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        KLog.d(TAG,"onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d(TAG,"onDestroy");
    }

    public class MyBinder extends Binder{
        public void showToastByService(String msg){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }
}

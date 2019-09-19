package com.jtl.aidl_service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.socks.library.KLog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import static com.jtl.aidl_service.Constant.HEIGHT;
import static com.jtl.aidl_service.Constant.WIDTH;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/19 21:32
 * 描述:
 * 更改:
 */
public class AIDLService extends Service {
    private static final String TAG=AIDLService.class.getSimpleName();
    private CameraWrapper mCameraWrapper;
    private IMyAidlInterface.Default.Stub mAidlInterface=new IMyAidlInterface.Stub() {
        @Override
        public void printLog(String msg) throws RemoteException {
            KLog.w(TAG,"printLog:"+msg);
        }

        @Override
        @RequiresPermission(Manifest.permission.CAMERA)
        public void openCamera(String cameraId) throws RemoteException {
            mCameraWrapper.openCamera(cameraId);
        }

        @Override
        public void closeCamera() throws RemoteException {
            mCameraWrapper.closeCamera();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mCameraWrapper=new CameraWrapper(getApplicationContext(),WIDTH,HEIGHT,true,null);
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
        return mAidlInterface;
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
}

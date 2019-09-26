package com.jtl.aidl_service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.socks.library.KLog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.util.Pools;

import static com.jtl.aidl_service.Constant.HEIGHT;
import static com.jtl.aidl_service.Constant.WIDTH;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/19 21:32
 * 描述:
 * 更改:
 */
public class AIDLService extends Service implements CameraWrapper.CameraDataListener {
    private static final String TAG = AIDLService.class.getSimpleName();
    private CameraWrapper mCameraWrapper;
    private volatile byte[] mData;
    private Pools.SynchronizedPool<CameraData> mDataSynchronizedPool = new Pools.SynchronizedPool<>(5);
    private Thread mCameraServiceThread;

    private RemoteCallbackList<CameraCallBack> mCallbackList = new RemoteCallbackList<>();

    private IMyAidlInterface.Default.Stub mAidlInterface = new IMyAidlInterface.Stub() {
        @Override
        public void printLog(String msg) throws RemoteException {
            KLog.w(TAG, "printLog:" + msg);
        }

        @Override
        public byte[] getCameraData() throws RemoteException {
            KLog.w(TAG, "回调---------AIDL------");
            return mData;
        }

        @Override
        @RequiresPermission(Manifest.permission.CAMERA)
        public void openCamera(String cameraId) throws RemoteException {
            KLog.w(TAG, "打开相机");
            mCameraWrapper.openCamera(cameraId);
        }

        @Override
        public void closeCamera() throws RemoteException {
            KLog.w(TAG, "关闭相机");
            mCameraWrapper.closeCamera();
        }

        @Override
        public void startPreview() throws RemoteException {
            if (mCameraServiceThread == null) {
                mCameraServiceThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
//                            CameraData cameraData=mDataSynchronizedPool.acquire();
//                            cameraData.
                            callBackData(mCameraWrapper.getImageData());
                        }
                    }
                });
            }
            mCameraServiceThread.start();
        }

        @Override
        public void register(CameraCallBack callBack) throws RemoteException {
            mCallbackList.register(callBack);
        }

        @Override
        public void unregister(CameraCallBack callBack) throws RemoteException {
            mCallbackList.unregister(callBack);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mCameraWrapper = new CameraWrapper(getApplicationContext(), WIDTH, HEIGHT, true, this);
        KLog.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d(TAG, "onBind");
        return mAidlInterface;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        KLog.d(TAG, "onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d(TAG, "onDestroy");
    }

    @Override
    public void setCameraDataListener(String mCameraId, byte[] imageData, float timestamp, int imageFormat) {
        KLog.w(TAG, "回调---------CameraWrapper------");
        mData = imageData.clone();
    }

    private void callBackData(byte[] cameraData) {
        try {
            //获取回调个数
            int N = mCallbackList.beginBroadcast();

            for (int i = 0; i < N; i++) {
                //把所有回调都进行相关操作
                mCallbackList.getBroadcastItem(i).callBack(cameraData);
            }
            //finish
            mCallbackList.finishBroadcast();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

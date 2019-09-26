package com.jtl.aidl_client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jtl.aidl_client.camera.CameraGLSurface;
import com.jtl.aidl_service.CameraCallBack;
import com.jtl.aidl_service.IMyAidlInterface;
import com.socks.library.KLog;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mAidlBindBtn;
    private Button mAidlTestBtn;
    private Button mOpenCameraBtn;
    private Button mCloseCameraBtn;

    private Thread mCameraThread;
    private CameraGLSurface mCameraGLSurface;
    private int width = Constant.WIDTH;
    private int height = Constant.HEIGHT;

    private IMyAidlInterface mIMyAidlInterface = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    CameraCallBack.Stub mCameraCallBack=new CameraCallBack.Stub() {
        @Override
        public void callBack(byte[] data) throws RemoteException {
            if (mCameraGLSurface!=null){
                mCameraGLSurface.setCameraData(Constant.CAMERA_BACK,data);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraGLSurface=findViewById(R.id.gl_aidl_preview);
        mAidlBindBtn = findViewById(R.id.btn_aidl_bind);
        mAidlTestBtn = findViewById(R.id.btn_aidl_test);
        mOpenCameraBtn = findViewById(R.id.btn_aidl_open);
        mCloseCameraBtn = findViewById(R.id.btn_aidl_close);

        addOnClickListener(mAidlBindBtn, mAidlTestBtn, mOpenCameraBtn, mCloseCameraBtn);

//        startCameraDataThread();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraGLSurface != null) {
            mCameraGLSurface.onResume();
            mCameraGLSurface.setAspectRatio(height, width);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraGLSurface != null) {
            mCameraGLSurface.onPause();
        }

        try {
            if (mIMyAidlInterface != null) {
                mIMyAidlInterface.closeCamera();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void addOnClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_aidl_bind:
                Intent intent = new Intent();
                intent.setPackage("com.jtl.aidl_service");
                intent.setAction("com.jtl.aidl_service");

                boolean isSuccess = bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

                Toast.makeText(MainActivity.this, isSuccess ? "bind success" : "bind failed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_aidl_test:
                try {
                    if (mIMyAidlInterface != null) {
                        mIMyAidlInterface.startPreview();
                        mIMyAidlInterface.register(mCameraCallBack);
                        mIMyAidlInterface.printLog("测试:" + Process.myPid());
                    } else {
                        Toast.makeText(MainActivity.this, "mIMyAidlInterface==null  " + Process.myPid(), Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_aidl_open:
                openCamera();
                break;
            case R.id.btn_aidl_close:
                closeCamera();
                break;
        }
    }

    private void openCamera() {
        try {
            if (mIMyAidlInterface != null) {
                mIMyAidlInterface.openCamera("0");
            } else {
                Toast.makeText(MainActivity.this, "mIMyAidlInterface==null  " + Process.myPid(), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        try {
            if (mIMyAidlInterface != null) {
                mIMyAidlInterface.closeCamera();
            } else {
                Toast.makeText(MainActivity.this, "mIMyAidlInterface==null  " + Process.myPid(), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startCameraDataThread() {
        mCameraThread = new Thread(new Runnable() {
            @Override
            public void run() {
                KLog.w(TAG, "startCameraDataThread");
                while (true) {
                    try {
                        if (mIMyAidlInterface == null || mIMyAidlInterface.getCameraData() == null) {
                            KLog.w(TAG, "mIMyAidlInterface==null||mIMyAidlInterface.getCameraData()==null");
                            continue;
                        }
                        KLog.w(TAG, "setCameraData");
                        mCameraGLSurface.setCameraData(Constant.CAMERA_BACK,mIMyAidlInterface.getCameraData());
                    } catch (RemoteException e) {
                        KLog.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        }, "CameraDataThread");

        mCameraThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }
}

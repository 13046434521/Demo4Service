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

import com.jtl.aidl_service.IMyAidlInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAidlBindBtn;
    private Button mAidlTestBtn;
    private Button mOpenCameraBtn;
    private Button mCloseCameraBtn;

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
    private String mCameraId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAidlBindBtn = findViewById(R.id.btn_aidl_bind);
        mAidlTestBtn = findViewById(R.id.btn_aidl_test);
        mOpenCameraBtn = findViewById(R.id.btn_aidl_open);
        mCloseCameraBtn = findViewById(R.id.btn_aidl_close);

        addOnClickListener(mAidlBindBtn, mAidlTestBtn, mOpenCameraBtn, mCloseCameraBtn);
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
                mCameraId = mCameraId == "0" ? "1" : "0";
                mIMyAidlInterface.openCamera(mCameraId);
            } else {
                Toast.makeText(MainActivity.this, "mIMyAidlInterface==null  " + Process.myPid(), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera(){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}

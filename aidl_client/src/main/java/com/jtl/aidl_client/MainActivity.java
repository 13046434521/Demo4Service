package com.jtl.aidl_client;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAidlBindBtn;
    private Button mAidlUnBindBtn;
//    private IMyAidlInterface mIMyAidlInterface=new IMyAidlInterface.Stub() {
//        @Override
//        public void printLog(final String msg) throws RemoteException {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this,msg+":"+ Process.myPid(),Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    };

    private IMyAidlInterface mIMyAidlInterface=null;
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMyAidlInterface=IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAidlBindBtn=findViewById(R.id.btn_aidl_bind);
        mAidlUnBindBtn=findViewById(R.id.btn_aidl_unbind);

        addOnClickListener(mAidlBindBtn,mAidlUnBindBtn);
    }

    private void addOnClickListener(View ... views) {
        for (View view:views){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_aidl_bind:
                Intent intent =new Intent();
//                intent.setClassName("com.jtl.aidl_service",com.jtl.aidl_service.IMyAidlInterface.class.getName());
                intent.setPackage("com.jtl.aidl_service");
                intent.setAction("com.jtl.aidl_service");

                boolean isSuccess= bindService(intent,mServiceConnection,BIND_AUTO_CREATE);

                Toast.makeText(MainActivity.this,isSuccess?"bind success":"bind failed",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_aidl_unbind:
                try {
                    if (mIMyAidlInterface!=null){
                        mIMyAidlInterface.printLog("测试:"+ Process.myPid());
                    }else{
                        Toast.makeText(MainActivity.this,"mIMyAidlInterface==null  "+ Process.myPid(),Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection!=null){
            unbindService(mServiceConnection);
        }
    }
}

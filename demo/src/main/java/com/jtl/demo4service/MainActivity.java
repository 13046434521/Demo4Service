package com.jtl.demo4service;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.socks.library.KLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mBindBtn;
    private Button mUnBindBtn;
    private EditText mDataEdit;
    private Button mTestBtn;
    private Connection mConnection;

    private DemoService.MyBinder mMyBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnection=new Connection();

        mStartBtn=findViewById(R.id.btn_service_start);
        mStopBtn=findViewById(R.id.btn_service_stop);
        mBindBtn=findViewById(R.id.btn_service_bind);
        mUnBindBtn=findViewById(R.id.btn_service_unbind);
        mTestBtn=findViewById(R.id.btn_service_test);
        mDataEdit=findViewById(R.id.et_service_data);

        addOnClickListener(mStartBtn,mStopBtn,mBindBtn,mUnBindBtn,mTestBtn);
    }

    private void addOnClickListener(View ... views) {
        for (View view:views){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_service_start:
                Intent startIntent=new Intent(this,DemoService.class);
                startService(startIntent);
                break;
            case R.id.btn_service_stop:
                Intent stopIntent=new Intent(this,DemoService.class);
                stopService(stopIntent);
                break;

            case R.id.btn_service_bind:
                Intent bindIntent=new Intent(this,DemoService.class);
                bindService(bindIntent,mConnection,BIND_AUTO_CREATE);
                break;
            case R.id.btn_service_unbind:
                unbindService(mConnection);
                break;
            case R.id.btn_service_test:
                if (!TextUtils.isEmpty(mDataEdit.getText())&&mMyBinder!=null){
                    mMyBinder.showToastByService(mDataEdit.getText().toString());
                }
                break;
        }
    }

    private class Connection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KLog.d(name.toString());
            //注意这里不能跨进程，否则转换会报错
            mMyBinder= (DemoService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KLog.d("onServiceDisconnected"+name.toString());
        }
    }
}

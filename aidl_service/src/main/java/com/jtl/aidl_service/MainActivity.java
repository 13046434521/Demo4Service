package com.jtl.aidl_service;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=new Intent();
        intent.setPackage("com.jtl.aidl_service");
        intent.setAction("com.jtl.aidl_service");
        startService(intent);
    }
}

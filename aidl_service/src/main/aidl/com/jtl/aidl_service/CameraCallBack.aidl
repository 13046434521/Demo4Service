// CameraCallBack.aidl
package com.jtl.aidl_service;

// Declare any non-default types here with import statements
// 手动 import  CameraData类
import com.jtl.aidl_service.CameraData;
interface CameraCallBack {
    void callBack(in byte[] data);
}

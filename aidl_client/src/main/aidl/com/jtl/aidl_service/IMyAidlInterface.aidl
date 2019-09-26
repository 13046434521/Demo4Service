// IMyAidlInterface.aidl
package com.jtl.aidl_service;

// Declare any non-default types here with import statements
// 手动 import  CameraCallBack
import com.jtl.aidl_service.CameraCallBack;

interface IMyAidlInterface {
    void printLog(String msg);

    byte[] getCameraData();

    void openCamera(String cameraId);

    void closeCamera();

    void startPreview();

    void register(CameraCallBack callBack);

    void unregister(CameraCallBack callBack);
}

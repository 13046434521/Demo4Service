// IMyAidlInterface.aidl
package com.jtl.aidl_service;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    void printLog(String msg);

    byte[] getCameraData();

    void openCamera(String cameraId);

    void closeCamera();

}

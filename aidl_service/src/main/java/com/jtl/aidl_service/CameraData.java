package com.jtl.aidl_service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/20 14:49
 * 描述:
 * 更改:
 */
public class CameraData implements Parcelable {
    public static final Creator<CameraData> CREATOR = new Creator<CameraData>() {
        @Override
        public CameraData createFromParcel(Parcel in) {
            return new CameraData(in);
        }

        @Override
        public CameraData[] newArray(int size) {
            return new CameraData[size];
        }
    };
    private String mCameraId;
    private byte[] imageData;
    private float timestamp;
    private int imageFormat;

    protected CameraData(Parcel in) {
        this.mCameraId = in.readString();
        this.imageData = in.marshall();
        this.timestamp = in.readInt();
        this.imageFormat = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCameraId);
        dest.writeByteArray(imageData);
        dest.writeFloat(timestamp);
        dest.writeInt(imageFormat);
    }
}

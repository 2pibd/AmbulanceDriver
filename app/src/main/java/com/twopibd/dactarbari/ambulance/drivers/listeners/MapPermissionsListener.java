package com.twopibd.dactarbari.ambulance.drivers.listeners;

public class MapPermissionsListener {

    public  static  myMapPermissionListener myPicUploadListener;
    public  static  interface myMapPermissionListener {
        String onPermissionGivenSucced();
        String onPermissionGivenFailed();

    }

    public static void setMyPicUploadListener(myMapPermissionListener listener) {
        myPicUploadListener = listener;
    }
}

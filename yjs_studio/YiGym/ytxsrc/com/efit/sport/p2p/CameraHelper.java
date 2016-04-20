package com.efit.sport.p2p;

import com.yuntongxun.ecsdk.CameraCapability;
import com.yuntongxun.ecsdk.CameraInfo;
import com.yuntongxun.ecsdk.ECDevice;

import java.util.Arrays;

/**
 * Created by tangtt on 2015/10/20.
 */
public class CameraHelper {
    private CameraInfo[] cameraInfos;
    private int numberOfCameras;

    private int defaultCameraId;

    private int cameraCurrentlyLocked;

    private int mCameraCapbilityIndex;
    private  static CameraHelper sInstance = null;


    public static CameraHelper instance() {
        if(sInstance == null){
            synchronized (CameraHelper.class) {
                if(sInstance == null) {
                    sInstance = new CameraHelper();
                }
            }
        }
        return sInstance;
    }

    private CameraHelper() {
        init();
    }
    private void init() {

        cameraInfos = ECDevice.getECVoIPSetupManager().getCameraInfos();

        // Find the ID of the default camera
        if (cameraInfos != null) {
            numberOfCameras = cameraInfos.length;
        }

        // Find the total number of cameras available
        for (int i = 0;
                i < numberOfCameras; i++) {
            if (cameraInfos[i].index == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                defaultCameraId = i;
                comportCapbilityIndex(cameraInfos[i].caps);
            }
        }
    }

    public void comportCapbilityIndex(CameraCapability[] caps) {

        if (caps == null) {
            return;
        }
        int pixel[] = new int[caps.length];
        int _pixel[] = new int[caps.length];
        for (CameraCapability cap : caps) {
            if (cap.index >= pixel.length) {
                continue;
            }
            pixel[cap.index] = cap.width * cap.height;
        }

        System.arraycopy(pixel, 0, _pixel, 0, caps.length);

        Arrays.sort(_pixel);
        for (int i = 0; i < caps.length; i++) {
            if (pixel[i] == /*_pixel[0]*/ 352 * 288 || pixel[i] == _pixel[0]) {
                mCameraCapbilityIndex = i;
                return;
            }
        }
    }

    public int getCameraCapbilityIndex() {
        return mCameraCapbilityIndex;
    }

    public int getDefaultCameraId() {
        return defaultCameraId;
    }

}

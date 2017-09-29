package com.cpm.Get_IMEI_number;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

/**
 * Created by deepakp on 9/27/2017.
 */

public class ImeiNumberClass extends AppCompatActivity {

    private TelephonyManager mTelephonyManager;
    Context context;

    public ImeiNumberClass(Context context) {
        this.context = context;
    }

    public String[] getDeviceImei() {

        String deviceid[] = null;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            deviceid = getSimImei();
        } else {
            return new String[]{"0"};
        }
        return deviceid;
    }


    public String[] getSimImei() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

        String imeiSIM1 = null, imeiSIM2 = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mTelephonyManager.getPhoneCount() > 1) {
                imeiSIM1 = telephonyInfo.getImeiSIM1();
                imeiSIM2 = telephonyInfo.getImeiSIM2();
                return new String[]{imeiSIM1, imeiSIM2};
            } else {
                imeiSIM1 = telephonyInfo.getImeiSIM1();
                return new String[]{imeiSIM1};
            }
        } else {
            if (telephonyInfo.isDualSIM()) {
                imeiSIM1 = telephonyInfo.getImeiSIM1();
                imeiSIM2 = telephonyInfo.getImeiSIM2();
                return new String[]{imeiSIM1, imeiSIM2};
            } else {
                imeiSIM1 = telephonyInfo.getImeiSIM1();
                return new String[]{imeiSIM1};
            }

        }


    }


}

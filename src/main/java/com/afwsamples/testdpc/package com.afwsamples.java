package com.afwsamples.testdpc;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerCommandsReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerCommandsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        try {
            switch (intent.getAction()) {
                case "com.testdpc.custom.TOGGLE_USB":
                    boolean enableUsb = intent.getBooleanExtra("enable", true);
                    dpm.setUsbDataSignalingEnabled(enableUsb);
                    Log.i(TAG, "USB data signaling set to: " + enableUsb);
                    break;
                // Add other cases here for more actions
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denial: " + intent.getAction(), e);
        } catch (Exception e) {
            Log.e(TAG, "Error executing DPM command for action: " + intent.getAction(), e);
        }
    }
}

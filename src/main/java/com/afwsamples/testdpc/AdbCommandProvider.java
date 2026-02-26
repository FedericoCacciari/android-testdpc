package com.afwsamples.testdpc;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.content.IntentFilter;

public class AdbCommandProvider extends ContentProvider {
    private static final String TAG = "AdbCommandProvider";

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        // Security check: Only ADB (2000) or Root (0)
        int callingUid = Binder.getCallingUid();
        if (callingUid != 2000 && callingUid != 0) {
            throw new SecurityException("Access denied! Only ADB can use this provider.");
        }

        DevicePolicyManager dpm = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = DeviceAdminReceiver.getComponentName(getContext());
        Bundle result = new Bundle();

        try {
            switch (method) {
                case "toggle_usb":
                    boolean enableUsb = extras != null ? extras.getBoolean("enable", true) : true;
                    dpm.setUsbDataSignalingEnabled(enableUsb);
                    result.putString("status", "USB set to " + enableUsb);
                    break;
                case "setup_bridge":
                    // This is to enable passing Intents from the personal profile to COPE
                    dpm.addCrossProfileIntentFilter(adminComponent,
                        new IntentFilter("com.testdpc.custom.TOGGLE_USB"),
                        DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED);
                    result.putString("status", "COPE bridge configured successfully!");
                    break;
                default:
                    result.putString("status", "Unknown command.");
                    break;
            }
        } catch (Exception e) {
            result.putString("status", "Error: " + e.getMessage());
        }
        return result;
    }

    // Boilerplate methods required by ContentProvider (not used)
    @Override public boolean onCreate() { return true; }
    @Override public Cursor query(Uri uri, String[] p, String s, String[] sa, String so) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues v) { return null; }
    @Override public int delete(Uri uri, String s, String[] sa) { return 0; }
    @Override public int update(Uri uri, ContentValues v, String s, String[] sa) { return 0; }
}

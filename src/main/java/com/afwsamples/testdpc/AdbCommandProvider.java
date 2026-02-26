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
        // CONTROLLO SICUREZZA: Blocca chiunque non sia ADB (UID 2000) o Root (UID 0)
        int callingUid = Binder.getCallingUid();
        if (callingUid != 2000 && callingUid != 0) {
            Log.e(TAG, "Tentativo di accesso non autorizzato da UID: " + callingUid);
            throw new SecurityException("Accesso negato! Solo ADB può usare questo provider.");
        }

        DevicePolicyManager dpm = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = DeviceAdminReceiver.getComponentName(getContext());
        Bundle result = new Bundle();

        try {
            switch (method) {
                case "toggle_usb":
                    boolean enableUsb = extras != null ? extras.getBoolean("enable", true) : true;
                    dpm.setUsbDataSignalingEnabled(enableUsb);
                    result.putString("status", "success");
                    result.putString("message", "USB impostata su " + enableUsb);
                    break;
                    
                case "setup_bridge":
                    // Crea il ponte COPE per consentire agli intent dal profilo personale di arrivare qui
                    dpm.addCrossProfileIntentFilter(adminComponent, 
                        new IntentFilter("com.testdpc.custom.TOGGLE_USB"), 
                        DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED);
                    result.putString("status", "success");
                    result.putString("message", "Ponte COPE configurato con successo!");
                    break;
                    
                default:
                    result.putString("status", "error");
                    result.putString("message", "Comando sconosciuto.");
                    break;
            }
        } catch (Exception e) {
            result.putString("status", "error");
            result.putString("message", "Errore DPM: " + e.getMessage());
        }
        return result;
    }

    // Boilerplate obbligatorio per ContentProvider (non usato)
    @Override public boolean onCreate() { return true; }
    @Override public Cursor query(Uri uri, String[] p, String s, String[] sa, String so) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues v) { return null; }
    @Override public int delete(Uri uri, String s, String[] sa) { return 0; }
    @Override public int update(Uri uri, ContentValues v, String s, String[] sa) { return 0; }
}
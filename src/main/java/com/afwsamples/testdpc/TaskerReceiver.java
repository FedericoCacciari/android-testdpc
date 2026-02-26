package com.afwsamples.testdpc;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TaskerReceiver extends BroadcastReceiver {
    private static final String TAG = "TaskerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        
        Log.i(TAG, "Comando ricevuto da Tasker: " + intent.getAction());

        // Otteniamo il Device Policy Manager
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        try {
            if ("com.testdpc.custom.TOGGLE_USB".equals(intent.getAction())) {
                // Leggiamo se Tasker ci ha chiesto di abilitare (true) o disabilitare (false) l'USB
                boolean enableUsb = intent.getBooleanExtra("enable", true);
                
                // Eseguiamo il comando da Device Owner (Richiede Android 12+)
                dpm.setUsbDataSignalingEnabled(enableUsb);
                Log.i(TAG, "Porta USB dati impostata su: " + enableUsb);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Errore di permessi. L'app non è Device Owner?", e);
        } catch (Exception e) {
            Log.e(TAG, "Errore generico durante l'esecuzione", e);
        }
    }
}
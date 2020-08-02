package com.example.marwa.launcher002.recievers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;




public class WifiStat extends BroadcastReceiver {
    private WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        final String action = intent.getAction();

        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean connected = info.isConnected();


        }

        wifiManager.setWifiEnabled(true);
        //////////////////////////////////////////





    }



}

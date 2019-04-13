package com.example.marwa.launcher002.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.model.AppDetail;
import com.example.marwa.launcher002.utils.WSadressIP;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NewInstalledApps extends Service {
    private Timer timer = new Timer();

    private PackageManager manager;
    private List<AppDetail> apps;

    String oldApp="com.example.marwa.launcher002";

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("oldApp", oldApp).apply();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getLastInstalledApp();   //Your code here
            }
        }, 0, 30000);//1 minute
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void getLastInstalledApp(){
        manager = getPackageManager();

        List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_META_DATA);

        Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
                return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
            }
        });
        String newApp =packages.get(0).packageName;
        Log.v("new installed apps :","0/ new-------------------- "+newApp);
       oldApp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
               .getString("oldApp", "defaultStringIfNothingFound");
        Log.v("new installed apps :","1/ old-------------------- "+oldApp);
       if(!newApp.equals(oldApp)){
            AddNewAppDB(newApp);
           Log.v("new installed apps :","2/ new-------------------- "+newApp);
            oldApp= newApp;
           Log.v("new installed apps :","3/ old-------------------- "+oldApp);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("oldApp", oldApp).apply();
           Log.v("new installed apps :","4/ old from shared-------------------- "+PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                   .getString("oldApp", "defaultStringIfNothingFound"));
       }

    }


    public void AddNewAppDB(final String packg){
        final String   URL = "http://"+ WSadressIP.WSIP+"/launcher/MAddNewApp.php";
        //  final String   URL =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/calls";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")) {
                    //
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");



                params.put("packg", packg);
                params.put("id_target", MainActivity.id_target);
                return params;
            }
        };
        Volley.newRequestQueue(NewInstalledApps.this).add(stringRequest);
    }
}

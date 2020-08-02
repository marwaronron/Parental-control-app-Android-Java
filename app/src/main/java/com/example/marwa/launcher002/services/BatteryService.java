package com.example.marwa.launcher002.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.utils.WSadressIP;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryService extends Service {
    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, filter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float)scale;
                float p = batteryPct * 100;


                if(batteryPct < 15){
                    //do your stuff
                }
                SendBatteryDB(String.valueOf(Math.round(p)));


            }
        }, 0, 60000);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }



    public void SendBatteryDB(final String value ) {


        final String   URL_Notif =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/targets/updatebattery?id_target="+MainActivity.id_target+"&battery="+value;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Notif,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Battery percentage","%%%%%%%%%%%%%%%%%%%% "+value);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");

                params.put("battery", value);
                params.put("id_target", MainActivity.id_target);
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(BatteryService.this).add(stringRequest);

    }

}

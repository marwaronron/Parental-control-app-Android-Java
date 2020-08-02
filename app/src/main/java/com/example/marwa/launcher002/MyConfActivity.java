package com.example.marwa.launcher002;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.services.MyTestService;
import com.example.marwa.launcher002.services.SendScreenShot;
import com.example.marwa.launcher002.utils.WSadressIP;
import com.sensorberg.permissionbitte.BitteBitte;
import com.sensorberg.permissionbitte.PermissionBitte;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.TimerTask;

public class MyConfActivity extends AppCompatActivity implements BitteBitte {

    List<String> RequiredApps ;
    EditText txttarget ;
    Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_conf);

        //: dont show conf interface
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (PermissionBitte.shouldAsk(this, this)) {
            PermissionBitte.ask(MyConfActivity.this, MyConfActivity.this);
        }else {
            txttarget = (EditText) findViewById(R.id.editText20190501);
            btnConfirm = (Button) findViewById(R.id.buttoncode);
            btnConfirm.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(txttarget.getText()!=null){
                                Start(txttarget.getText().toString());
                            }
                        }
                    }
            );

        }









    }


    public void Start(String target_id){

        AddTargetDB(target_id);
      /*  //1: id target


        //2: Phone Model
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.d("hi","+++++++ Man: "+manufacturer+" Model: "+model);

        //3: Phone apps
        final PackageManager manager = getPackageManager();

        String Ary[] = {"CAMERA","GALLERY","PHOTO","PHOTOS","PHONE","DIALER","CONTACTS","MESSAGES","MESSAGING","DRIVE",
                "CALENDAR","CALCULATOR","PLAYSTORE","CHROME","Clock","SETTINGS","RADIO"};

        RequiredApps = Arrays.asList(Ary);


        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            if (!isSystemPackage(p) || AppNameVal(appName)) {

                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String appPackage = p.applicationInfo.packageName;
                //String hi = p.applicationInfo.sourceDir;


                Log.d("---------","label: "+appPackage+" nom: "+appName);
                String encodedImage = encodeToBase64(getBitmapFromDrawable(icon), Bitmap.CompressFormat.JPEG, 100);
                AddNewAppDB(appPackage,encodedImage,appName);
            }
        }*/



    }
    private  boolean AppNameVal(String appName){

        if(appName !=null){

            for (String a : RequiredApps) {
                if(appName.toUpperCase().equals(a)){
                    return true;
                }
            }

        }

        return false;
    }
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public void AddNewAppDB(final String packg , final String icon, final String nom){

        final String   URL_b =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/apps/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_b, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")) {
                    Log.d("hello","new installed app is here vvvvvvvvvvvv");
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

                params.put("nom", nom);
                params.put("icon", icon);
                params.put("packg", packg);
                params.put("id_target",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id_target", "3"));
                return params;
            }
        };
        Volley.newRequestQueue(MyConfActivity.this).add(stringRequest);
    }

    public void AddTargetDB(final String key){

        final String   URL_A =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/targets/gettargetid?key="+key;
        final Integer[] statee = new Integer[1];

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_A,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ok","......... "+response);
                        if(!response.equals("")){
                            statee[0] = Integer.parseInt(response);
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("id_target", ""+statee[0]).apply();
                            GetApps();
                            SendPhoneModel();
                            ////////////////////
                            SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = pref.edit();
                            ed.putBoolean("activity_executed", true);
                            ed.commit();
                            Intent i = new Intent(MyConfActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }else{

                            //
                            Toast.makeText(getApplicationContext(),"Wrong Code!",Toast.LENGTH_LONG).show();
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



                return params;
            }
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(MyConfActivity.this).add(stringRequest);
    }

    public void GetApps(){
        final PackageManager manager = getPackageManager();

        String Ary[] = {"CAMERA","GALLERY","PHOTO","PHOTOS","PHONE","DIALER","CONTACTS","CONTACT","MESSAGES","MESSAGE","MESSAGING","DRIVE",
                "CALENDAR","CALCULATOR","PLAYSTORE","CHROME","Clock","SETTINGS","RADIO"};

        RequiredApps = Arrays.asList(Ary);


        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            Log.d("---------","label: "+p+" nom: "+appName);
            if ((!p.toString().toUpperCase().contains("SAMSUNG"))&& !isSystemPackage(p) || ( AppNameVal(appName)&&(!p.toString().toUpperCase().contains("SAMSUNG"))) || appName.toString().toUpperCase().equals("CONTACTS") || appName.toString().toUpperCase().equals("MESSAGES" ) ) {

                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String appPackage = p.applicationInfo.packageName;
                //String hi = p.applicationInfo.sourceDir;



                String encodedImage = encodeToBase64(getBitmapFromDrawable(icon), Bitmap.CompressFormat.JPEG, 100);
                AddNewAppDB(appPackage,encodedImage,appName);
            }
        }
    }

    @Override
    public void yesYouCan() {

        txttarget = (EditText) findViewById(R.id.editText20190501);
        btnConfirm = (Button) findViewById(R.id.buttoncode);
        btnConfirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txttarget.getText()!=null){
                            Start(txttarget.getText().toString());
                        }
                    }
                }
        );

    }

    @Override
    public void noYouCant() {
        Toast.makeText(this, "Permission denied ! ", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void askNicer() {
        new AlertDialog.Builder(this)
                .setTitle("Permission(s)")
                .setMessage("You Need to Allow Permession(s)  ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionBitte.ask(MyConfActivity.this , MyConfActivity.this );
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyConfActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void SendPhoneModel(){
      final  String manufacturer = Build.MANUFACTURER;
       final String model = Build.MODEL;
        Log.d("hi","+++++++ Man: "+manufacturer+" Model: "+model);


        final String   URL_c =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/targets/setdevicedata?id_target="
                +PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id_target", "3")+"&model="+manufacturer +" "+model+"&battery=100";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_c, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")) {
                    Log.d("hello","phone model sent");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("hello","phone model NOT sent");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");

               // params.put("model", model);

                //params.put("id_target",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id_target", "3"));
                return params;
            }
        };
        Volley.newRequestQueue(MyConfActivity.this).add(stringRequest);

    }
}

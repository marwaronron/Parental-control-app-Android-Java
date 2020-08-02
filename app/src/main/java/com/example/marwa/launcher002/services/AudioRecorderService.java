package com.example.marwa.launcher002.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.marwa.launcher002.MainActivity.id_target;

public class AudioRecorderService extends Service {
    private Timer timer = new Timer();
    String oldRecordSate="0";
boolean isRecording = false;

    public Button play, stopx, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;





    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        //1:shared pref
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("oldRecordState", "0").apply();

        //2: audio recorder
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);


      //3: thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getAudioRecordState();   //Your code here
            }
        }, 0, 10000);//20 seconds
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

//chouf l parent requesta l recording wela amal stop recording
   private static final String URL_Activitiess = "http://"+ WSadressIP.WSIPChokoA+"/kidslanch_serv/web/index.php/screens/getchild?id_target="+ MainActivity.id_target;
    private void getAudioRecordState() {

        final Integer[] statee = new Integer[1];


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Activitiess,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                           statee[0] = Integer.parseInt(response);



                            oldRecordSate = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getString("oldRecordSate", "defaultStringIfNothingFound");

                        if (statee[0] == 1 && isRecording==false) {
                                oldRecordSate = "1";
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                        .putString("oldRecordSate", oldRecordSate).apply();
                                try {
                                    startRecording();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                isRecording= true;

                        } else if (statee[0] == 0 && isRecording) {
                                oldRecordSate = "0";
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                        .putString("oldRecordSate", oldRecordSate).apply();
                                try {
                                    stopRecording();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                isRecording = false;
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("record audio","øøøøøøøøøøøøøøøøøøøøøøøøøøøøø 4 "+error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                //params.put("id_target", id_target);

                return params;
            }
        };;

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
       // Volley.newRequestQueue(AudioRecorderService.this).add(stringRequest);
    }

    public void startRecording() throws IOException {
        Log.v("record audio","øøøøøøøøøøøøøøøøøøøøøøøøøøøøø is recording now");
        myAudioRecorder.prepare();
        myAudioRecorder.start();

    }

    public void stopRecording() throws IOException {
        Log.v("record audio","øøøøøøøøøøøøøøøøøøøøøøøøøøøøø stoped recording now");
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String formattedDate = df.format(c);

        saveAudioB(Environment.getExternalStorageDirectory() + "/recording.3gp",formattedDate);
    }
    public void saveAudioB(String selectedPath,String dateAdded) throws IOException {

        byte[] audioBytes;
        try {

            // Just to check file size.. Its is correct i-e; Not Zero
            File audioFile = new File(selectedPath);
            long fileSize = audioFile.length();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(selectedPath));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();

            // Here goes the Base64 string
            final String audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            saveAudioDB(audioBase64,dateAdded);
        } catch (Exception e) {
            //  DiagnosticHelper.writeException(e);
        }

    }
// ba3d stop recording sob l audio format base64 fil BASE
    public void saveAudioDB(final String audioBase64,final String dateAdded){

       // final String URL = "http://"+ WSadressIP.WSIP+"/launcher/MAddAudio.php";


        final String URL = "http://"+ WSadressIP.WSIPChokoA+"/kidslanch_serv/web/index.php/audios/add";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("record audio","øøøøøøøøøøøøøøøøøøøøøøøøøøøøø saved in db now");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");


                params.put("id_target", id_target);
                params.put("img_file",audioBase64);



                params.put("datee",dateAdded);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}

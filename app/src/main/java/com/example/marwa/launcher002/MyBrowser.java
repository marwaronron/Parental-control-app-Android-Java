package com.example.marwa.launcher002;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.services.MyTestService;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBrowser extends AppCompatActivity {
    EditText txtUrl;
    ImageView GoToUrl, Close;
    WebView MyWebView;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    //public static final String URL_REGEX = "^((www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(View.STATUS_BAR_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_my_browser);


        txtUrl = (EditText) findViewById(R.id.editText);
        GoToUrl = (ImageView) findViewById(R.id.imageView4);
        MyWebView = (WebView) findViewById(R.id.webView1);
        MyWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

               // Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
               // Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                txtUrl.setText(url);
                //when url change check safety again
                CheckSafetyDB(txtUrl.getText().toString());

                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        Close = (ImageView) findViewById(R.id.imageView7);




        // when u type text hide top bar
        txtUrl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportActionBar().hide();
                        getWindow().addFlags(View.STATUS_BAR_HIDDEN);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) ;
                    }
                }
        );

        // when u finsih typing text hide keyboard
        txtUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    getSupportActionBar().hide();
                    getWindow().addFlags(View.STATUS_BAR_HIDDEN);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) ;
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) ;
                }
            }
        });

        //button
       GoToUrl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!txtUrl.getText().equals("")){
                            Log.v("lllllllllllllll",txtUrl.getText().toString());

                            //hide keyboard
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                            // check url form is correct typed
                            Pattern p = Pattern.compile(URL_REGEX);
                            Matcher m = p.matcher(txtUrl.getText().toString());//replace with string to compare

                            if(m.find()) {
                                System.out.println("String contains URL");
                                CheckSafetyDB(txtUrl.getText().toString());


                            }else{
                                Toast.makeText(MyBrowser.this,"Enter a valide Website",Toast.LENGTH_SHORT).show();
                                System.out.println("no result found");
                            }

                        }
                    }
                }
        );


       Close.setOnClickListener(
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       finish();
                   }
               }
       );
    }




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
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }


    }


    final boolean[] isSafe = {false};

    private void CheckSafetyDB(final String typedWeb) {
        String lk = typedWeb.replace("https://","");


        final String   URL_Activities =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/blockedwebs/checklink?id_target="+MainActivity.id_target+"&link="+lk;

        final Integer[] statee = new Integer[1];

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Activities,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);



                            // ken l9it resultat fil response affichi l url
                          if(array.length() > 0){
                              MyWebView.getSettings().setJavaScriptEnabled(true);

                              MyWebView.loadUrl(txtUrl.getText().toString());


                          }else{
                              MyWebView.loadUrl("http://www.esprit.tn");
                              Toast.makeText(MyBrowser.this,"Forbidden Access",Toast.LENGTH_SHORT).show();
                              NotifDB(typedWeb);
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

                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(MyBrowser.this).add(stringRequest);

    }


    final String   URL_Notif =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/notifs/add";

    private void NotifDB(final String typedWeb) {



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
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                String formattedDate = df.format(c);
                params.put("date", formattedDate);
                params.put("content", "Access Try To:"+ typedWeb);
                params.put("id_target", MainActivity.id_target);
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(MyBrowser.this).add(stringRequest);

    }


}

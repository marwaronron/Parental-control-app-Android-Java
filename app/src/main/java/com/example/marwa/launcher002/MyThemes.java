package com.example.marwa.launcher002;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyThemes extends AppCompatActivity {
public TextView txt01, txt02, txt03, txt04 , txt05 , txt06;
ImageView img01 , img02, img03 , img04 , img05 , img06 , imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(View.STATUS_BAR_HIDDEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_my_themes);
       final LinearLayout helloBackground , topNav;
        helloBackground = MainActivity.getInstance().findViewById(R.id.background);
        topNav = (LinearLayout) MainActivity.getInstance().findViewById(R.id.myvie);

        img01 = (ImageView)findViewById(R.id.okfine);
        img02 =  (ImageView)findViewById(R.id.imageView5);
        img03 =  (ImageView)findViewById(R.id.imageView6);
        img04 =  (ImageView)findViewById(R.id.imageView7);
        img05 =  (ImageView)findViewById(R.id.imageView8);
        img06 =  (ImageView)findViewById(R.id.imageView9);
        imgBack = findViewById(R.id.imageView11);

        img01.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th01").apply();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m15);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorA));
                        }
                    }
                }
        );

        img02.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th02").apply();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m16);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorAccent));
                        }
                    }
                }
        );

        img03.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th03").apply();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m10);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorB));
                        }
                    }
                }
        );
        img04.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th04").apply();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m05);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorC));
                        }
                    }
                }
        );
        img05.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th05").apply();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m11);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorD));
                        }
                    }
                }
        );
        img06.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("theme", "th06").apply();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            helloBackground.setBackgroundResource(R.drawable.m04);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            topNav.setBackgroundColor(getColor(R.color.colorE));
                        }
                    }
                }
        );

        imgBack.setOnClickListener(
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
}

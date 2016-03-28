package com.daniel.apps.wearapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Daniel on 1/8/2016.
 */
/*Activity will get the delay and start the
  Countdown Service*/
public class TimeActivity extends Activity {

    //app
    String packageName ="";//will contain packagname of the selected app
    String appName;//holds the app label

    //layout/countdown
    NumberPicker nums;//seconds number picker
    NumberPicker mins;//minutes number picker
    NumberPicker hours;//hours number picker
    int lenghtoft;//holds the number of seconds in the countdown
    int lenghtofm;//holds the number of minutes in the countdown
    int lenghtofh;//holds the number of hours in the countdown

    //exit button
    public void exit(){
        Button exit = (Button)findViewById(R.id.back);
        exit.bringToFront();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
       //necessary to asign the proper layout to the proper devices
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub2);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                exit();

                packageName = getIntent().getStringExtra("AppName");

                PackageManager pm = getPackageManager();

                try {
                    //get applicationinfo
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);

                    //get then set name inside a textview
                    appName = String.valueOf(pm.getApplicationLabel(applicationInfo));

                    TextView name = (TextView) findViewById(com.daniel.apps.wearapp.R.id.textView);
                    name.bringToFront();
                    name.setText(appName);

                    //get then set icon inside an imagview
                    Drawable icon = pm.getApplicationIcon(applicationInfo);

                    ImageView iconPlace = (ImageView) findViewById(com.daniel.apps.wearapp.R.id.imageView2);
                    iconPlace.setImageAlpha(75);
                    iconPlace.setImageDrawable(icon);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.errinfo,Toast.LENGTH_LONG).show();
                }

                time();

                startit();
            }
        });
    }

    //method to start the countdown service
    public void startit() {
        Button start = (Button)findViewById(R.id.button);
        start.bringToFront();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        //calculate total delay time in milliseconds
        final int finalt = lenghtofh*3600000 + lenghtofm*60000+lenghtoft*1000;

        Intent go = new Intent(TimeActivity.this,CountdownService.class);
        go.putExtra("time",finalt);
        go.putExtra("name", packageName);
        go.putExtra("appname", appName);

        TimeActivity.this.startService(go);

        Toast.makeText(getApplicationContext(), R.string.willbe,Toast.LENGTH_LONG).show();

        finish();
            }
        });
    }

    //customize the number pickers so that they are visible
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();

        for(int i = 0; i < count; i++){

            View child = numberPicker.getChildAt(i);

            if(child instanceof EditText){

                try{
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);

                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);

                    ((EditText)child).setTextColor(color);

                    numberPicker.invalidate();

                    return true;
                }

                catch(Exception ignored){/*this won't happen*/}
            }
        }
        return false;
    }

    //method to handle the number pickers
    public void time() {
        nums = (NumberPicker) findViewById(R.id.numberPicker3);
        nums.bringToFront();
        setNumberPickerTextColor(nums, Color.parseColor("#000000"));

        mins = (NumberPicker) findViewById(R.id.numberPicker2);
        mins.bringToFront();
        setNumberPickerTextColor(mins, Color.parseColor("#000000"));

        hours = (NumberPicker) findViewById(R.id.numberPicker);
        hours.bringToFront();
        setNumberPickerTextColor(hours, Color.parseColor("#000000"));

        lenghtoft = 0;//holds the number of seconds in the countdown
        lenghtofm = 0;//holds the number of minutes in the countdown
        lenghtofh = 0;//holds the number of hours in the countdown

        nums.setMaxValue(59);
        nums.setMinValue(0);
        nums.setWrapSelectorWheel(true);
        nums.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                lenghtoft = newVal;
            }
        });

        final String[] stringArray = new String[60];

        int n = 1;

        for (int i2 = 0; i2 < 60; i2++) {
            stringArray[i2] = Integer.toString(n);

            n += 1;

            mins.setMaxValue(59);
            mins.setMinValue(0);
            mins.setWrapSelectorWheel(true);
            mins.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    lenghtofm = newVal;
                }
            });
        }

        final String[] minArray = new String[25];

        int min = 1;

        for (int mi = 0; mi < 25; mi++) {

            minArray[mi] = Integer.toString(min);

            min += 1;

            hours.setMaxValue(24);
            hours.setMinValue(0);
            hours.setWrapSelectorWheel(true);
            hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    lenghtofh = newVal;
                }
            });
        }

        final String[] hourArray = new String[60];

        int hour = 1;

        for (int housr = 0; housr < 60; housr++) {
            hourArray[housr] = Integer.toString(hour);

            housr += 1;
        }
    }
}

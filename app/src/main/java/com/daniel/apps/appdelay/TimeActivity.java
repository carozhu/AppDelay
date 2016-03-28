package com.daniel.apps.appdelay;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Daniel on 1/8/2016.
 */
public class TimeActivity extends AppCompatActivity{

   //app
    String packageName =""; //packagename
    String appName;//app label

    //delay
    NumberPicker nums;//NumberPicker for seconds
    NumberPicker mins;//NumberPicker for minutes
    NumberPicker hours;//NumberPicker for hours
    int lenghtoft;//holds the seconds for the timer
    int lenghtofm;//holds the minutes for the timer
    int lenghtofh;//holds the hours for the timer

    //time
    double offset;//offset for the date
    int SetMinute;//set minute for the date
    int SetHour;//set hour for the date

    //date
    int Setyear;//set year for the date
    int Setmonth;//set month for the date
    int Setday;//set day for the date

    //other
    Intent go;//intent to launch the service which will launch the app after the delay
    DateTime dt;//jodaTime Calendar
    boolean TD = false; //to monitor whether date of time was setup (false = timer, true=date)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       //enable the use of jodaTime
        JodaTimeAndroid.init(this);

        //set the contentView to activity_main
        setContentView(R.layout.activity_main);

        //get the packagename from intent
        packageName = getIntent().getStringExtra("AppName");

       //try to change the text of the actionbar
        try{
            ActionBar actbar = getSupportActionBar();
            actbar.setTitle(R.string.wait);
            actbar.show();
        }catch (NullPointerException ignored){}
        //get the packagemanger
        PackageManager pm = getPackageManager();
       //use it to find the icon and label
        try {
            //get applicationinfo
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);

            //get app's label
            appName = String.valueOf(pm.getApplicationLabel(applicationInfo));

           // /put the label into a textview
            TextView name = (TextView)findViewById(R.id.AppName2);
            name.setText(appName);

            //get icon
            Drawable icon = pm.getApplicationIcon(applicationInfo);

            //put the icon into an imageview
            ImageView iconPlace = (ImageView)findViewById(R.id.AppIcon2);
            iconPlace.setImageDrawable(icon);

        } catch (Exception e) {
         //error this should not happen
           Toast.makeText(getApplicationContext(), R.string.errRetriving,Toast.LENGTH_LONG).show();
        }

        //instantiate the two relative layouts for the date and timer cards
        final RelativeLayout TimerL = (RelativeLayout)findViewById(R.id.TimerLayout);//timer card
        final RelativeLayout DateL = (RelativeLayout)findViewById(R.id.DateLayout);//date card

        //instantiate the radioGroup and it's two radioButtons to switch between the functions
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
             case R.id.timer:
                 DateL.setVisibility(View.GONE);//make the date card invisible
                 TimerL.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left));//start the animation
                 TimerL.setVisibility(View.VISIBLE);//make the timer card visible
                 TD =false;//set TD to false meaning that timer is now visible
                 break;

             case R.id.date:
                 TimerL.setVisibility(View.GONE);//make the timer card invisible
                 DateL.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right));//start the animation
                 DateL.setVisibility(View.VISIBLE);//make the timer card visible
                 TD = true;//set TD to false meaning that timer is now visible
                 break;
               }
            }
        });

        //for the wait until a certain date function
        date();
        //countdown timer function
        time();
        //start app method
        startit();

    }

    //method to start the Countdown Service(it will wait then start the app)
    public void startit() {
       //instantiate the start button
        Button start = (Button)findViewById(R.id.button);

        //onclick for the button
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      //false = timer, true =date
         if (!TD) {
             //timer
             final int finalt = lenghtofh * 3600000 + lenghtofm * 60000 + lenghtoft * 1000;//calculate the total delay in milliseconds

             //intent to start the service
             go = new Intent(TimeActivity.this, CountdownService.class);
             go.putExtra("time", finalt);//put in the delay
             go.putExtra("name", packageName);//put in the packagename
             go.putExtra("appname", appName);//put in the label

             TimeActivity.this.startService(go);//start the service

             //show a toast
             Toast.makeText(getApplicationContext(), R.string.launched, Toast.LENGTH_LONG).show();

             //finish this activity
             finish();
         } else {
             //date
             launchWithDate();//method to start the service with the date
         }
            }
        });
    }


    //set up the numberpickers for the timer
    public void time() {
        //numberPickers to pick the countdown
        nums = (NumberPicker) findViewById(R.id.numberPicker);
        mins = (NumberPicker) findViewById(R.id.minsPicker);
        hours = (NumberPicker) findViewById(R.id.hoursPicker);

        //int variables to store their values
        lenghtoft = 0;
        lenghtofh = 0;
        lenghtofm = 0;

        //seconds numberPicker
        nums.setMaxValue(59);//max value 59s
        nums.setMinValue(0);//min value 0s
        nums.setWrapSelectorWheel(true);

        nums.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                lenghtoft = newVal;//set the lenghtoft (remaining secs) equal to the new value (set by user)
            }
        });

        final String[] stringArray = new String[60];//array to hold the numbers of the number picker

        int n = 1;//keeps track of the current number

        for (int i2 = 0; i2 < 60; i2++) {//loop through all numbers
            stringArray[i2] = Integer.toString(n);//set the item number i2 equal to n
            n += 1;// increment n

            //minutes numberPicker
            mins.setMaxValue(59);//max value 59m
            mins.setMinValue(0);//min value 0m
            mins.setWrapSelectorWheel(true);
            mins.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    lenghtofm = newVal;//set the lenghtofm (remaining mins) equal to the new value (set by user)
                }
            });
        }

        final String[] minArray = new String[25];//array to hold the numbers of the number picker

        int min = 1;//keep track of the current number

        for (int mi = 0; mi < 25; mi++) {//loop through all numbers
            minArray[mi] = Integer.toString(min);//set the item number min equal to mi
            min += 1;// increment m

            //hours numberPicker
            hours.setMaxValue(24);//max value 24h
            hours.setMinValue(0);//min value 0h
            hours.setWrapSelectorWheel(true);
            hours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    lenghtofh = newVal;//set the lenghtofm (remaining mins) equal to the new value (set by user)
                }
            });
        }

        final String[] hourArray = new String[60];//array to hold the numbers of the number picker

        int hour = 1;//keep track of all numbers

        for (int housr = 0; housr < 60; housr++) {//loop through all numbers
            hourArray[housr] = Integer.toString(hour);//set the item number min equal to mi
            housr += 1;//increment housr
        }
    }

    //setup the date function
    public void date(){
      //setup joda.DateTime for later use
        try {
            dt = new DateTime().now();
        } catch (Exception ignored) {}

        //get the offset (daylight savings time)
        offset = 0.0;

       //get the Calendar Instance
        Calendar cal = Calendar.getInstance();

        //get Timezone
        TimeZone tz = TimeZone.getDefault();

        //boolean to get whether or not to use daylight savings time
        boolean t = tz.inDaylightTime(cal.getTime());

       //if yes get offset
        if (t)
            offset = ((TimeZone.getDefault().getRawOffset()) / (60 * 60 * 1000D) + (TimeZone.getDefault().getDSTSavings() / (60 * 60 * 1000D)));
      //else don't
        else
            offset = ((TimeZone.getDefault().getRawOffset()) / (60 * 60 * 1000D));

        //button to set the date (brings up calendar)
        final Button date = (Button)findViewById(R.id.setDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //date
                Setyear = dt.getYear();//get current year
                Setmonth = dt.getMonthOfYear();//get current month
                Setday = dt.getDayOfMonth();//get current day

                //show datepickerdialog with current date
                DatePickerDialog datePickerDialog = new DatePickerDialog(TimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Setyear = year;//set future year
                        Setmonth = monthOfYear;//set future month
                        Setday = dayOfMonth;//set future day
                        date.setText("Change Date\n" + Setyear + "/" + Setmonth + "/" + Setday);//set the preview in the button
                    }
                }, Setyear, Setmonth - 1 /*one array starts 0 zero the other at 1*/, Setday);

                datePickerDialog.show();//show dialog
            }
        });

        //button to set up the time (brings up clock)
       final Button Time = (Button)findViewById(R.id.SetTime);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetHour = dt.getHourOfDay();//get current hour
                SetMinute = dt.getMinuteOfHour();//get current minute

                //show timepickerdialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(TimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SetMinute = minute;  //set future minute
                        SetHour = hourOfDay; //set future hour (24 hour clock)
                       Time.setText("Change Time\n" + SetHour + ":" + SetMinute);//set preview
                    }
                }, (int) (SetHour+offset-1) /*take into account daylight savings time and the difference between the arrays*/,SetMinute,false);
                timePickerDialog.show();
            }
        });
    }

    //launch the launching Service with extras
    public void launchWithDate(){
      Intent go = new Intent(TimeActivity.this,CountdownService.class);//intent that will start the Service
        go.putExtra("day",Setday);//add the day
        go.putExtra("month",Setmonth);//add the month
        go.putExtra("year",Setyear);//add the year
        go.putExtra("hour",SetHour);//add the hour
        go.putExtra("minute",SetMinute);//add the minute

        go.putExtra("name", packageName);//add the app packageName
        go.putExtra("appname", appName);//add the app label

        TimeActivity.this.startService(go);//start the Service

        Toast.makeText(getApplicationContext(), R.string.launched,Toast.LENGTH_LONG).show();//show a Toast

        finish();//finish activity
    }

    //overrride the finish to release jodaTime
    @Override
    public void finish() {
        super.finish();
        //release the jodaTime
        dt = null;
    }
}

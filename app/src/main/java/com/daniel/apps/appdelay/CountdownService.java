package com.daniel.apps.appdelay;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Daniel on 12/18/2015.
 */
/*This Service is used to start the app. It runs in the background
 and once the timer is up or the certain date is reached, it will
 launch the app*/
public class CountdownService extends IntentService {

    //used for launching app
    String packageName = "";//will store the package name for the app that will be launched
    int time = 0;//the waiting time before launching the app
    String appName;//app label
    CountDownTimer ct;//countdown timer for delaying the launch

    //used for the notification
    PendingIntent pendingIntent;//pending intent for a notification button
    PendingIntent pendintIntent;//pending intent for a notification button
    Notification notify;//the notification
    NotificationCompat.Builder builder;//notification builder
    Random ran = new Random();//Random notification id generator
    int notificationId = ran.nextInt();//uses ran to get notification id
    boolean sendNotifs = true;//boolean to monitor whether or not to push notifications
    NotificationManagerCompat mgr;//notification manager

    //date function
    int Day = 0;//day in month
    int Month = 0;//month in year
    int Year = 0;//year
    int Hour = 0;//hour in day
    int Minute = 0;//minute in hour
    DateTime dt;//jodaTime variable to get the date

    //notification dismiss button onclick
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";

    public CountdownService() {
        super("");
    }

    public void setupNotif() {
        notify = new Notification();
        notify.when = 0;
        //starts the service in foreground so that Android doesn't kill it
        startForeground(notificationId, notify);

        //set up notification "stop" button
        Intent inte = new Intent(APPLICATION_STOPPED_ACTION);
        registerReceiver(receiver2, new IntentFilter(APPLICATION_STOPPED_ACTION));
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, inte, 0);

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
            //set up notification dismiss button
            Intent inten = new Intent(NOTIFICATION_DELETED_ACTION);
            registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
            pendintIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, inten, 0);

            //setup notification
            builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.drawable.x, getString(R.string.Dismiis), pendintIntent)
                    .addAction(R.drawable.stop, getString(R.string.stop), pendingIntent);

        }else{
            //setup notification without dismiss
            builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.drawable.stop, getString(R.string.stop), pendingIntent);
        }
        //setup the manager
        mgr = NotificationManagerCompat.from(getApplicationContext());
    }

    public void onHandleIntent(Intent intent) {
        //setup notification
        setupNotif();

        //start timer
        ct = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //calculate and show time remaining
                sendNotify(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                try {
                   //start app
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    startActivity(launchIntent);

                    //destroy service
                    onDestroy();

                } catch (Exception e) {
                    //shoe failed toast message
                    Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_LONG).show();
                }
            }
        }.start();
    }

    //broadcast receiver used for dismiss action in notification
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //stop foreground and remove notification
            sendNotifs = false;//stop sending notifications

            stopForeground(true);//stop foreground

            unregisterReceiver(receiver);//unregister receiver
            unregisterReceiver(receiver2);//unregister receiver2

            mgr.cancel(notificationId);
        }
    };

    //broadcast receiver used for stop app launch action in notification
    private static final String APPLICATION_STOPPED_ACTION = "APPLICATION_STOPPED";

    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //stop foreground and remove notification
           onDestroy();
        }
    };

    //calculates then pushes a notification with the time remaining
    public void sendNotify(long left) {
       if (sendNotifs) {
           //get time left in proper format
           long second = (left / 1000) % 60;
           long minute = (left / (1000 * 60)) % 60;
           long hour = (left / (1000 * 60 * 60)) % 24;

           String timet = String.format("%02d:%02d:%02d", hour, minute, second);//apply formatting

           builder.setContentText(getString(R.string.time_left) + appName + " in: " + "\n" + timet);//set the notification test

           mgr.notify(notificationId, builder.build());//update notification
       }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //enable the use of jodaTime
            JodaTimeAndroid.init(this);

            //get the intent values for packagename, app label and the delay period
            packageName = intent.getStringExtra("name");
            appName = intent.getStringExtra("appname");
            time = intent.getIntExtra("time", 0);

            //if time == 0, date was set
            if (time != 0) {
                //time set, use the countdown method
                onHandleIntent(intent);

            } else {
                //time not set use date method
                Day = intent.getIntExtra("day", 0);//get the day of month from intent
                Month = intent.getIntExtra("month", 0);//get the month of year from intent
                Year = intent.getIntExtra("year", 0);//get the year from intent
                Hour = intent.getIntExtra("hour", 0);//get the hour of day from intent
                Minute = intent.getIntExtra("minute", 0);//get the minute of hour from intent

                //method that will use this and wait
                launchWithDate();
            }

        } catch (NullPointerException e) {
            //error, this should never happen but incase show toast
            Toast.makeText(getApplicationContext(), R.string.err,Toast.LENGTH_LONG).show();
        }

        //helps with the stability
        return START_STICKY;
    }

    public void launchWithDate() {
        //setup the alarmmanger, it will do the waiting
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        //setup the calendar
        Calendar futureDate = Calendar.getInstance();

        //put in the proper values
        futureDate.clear();//make sure that it is clear
        futureDate.setTimeZone(TimeZone.getDefault());//get timezone
        futureDate.set(Calendar.DAY_OF_MONTH, Day);//get day
        futureDate.set(Calendar.MONTH, Month);//get month
        futureDate.set(Calendar.YEAR, Year);//get year

        futureDate.set(Calendar.HOUR_OF_DAY, Hour);//get hour
        futureDate.set(Calendar.MINUTE, Minute);//get minute

        //get current time
        dt = new DateTime().now();

        //compare the two, to get th remaining time
        long left = futureDate.getTimeInMillis() - dt.getMillis();

        Intent intent = new Intent(DATE_START_ACTION);//setup the intent for the alarm
        registerReceiver(MyAppReciever, new IntentFilter(DATE_START_ACTION));//register the receiver to handle the alarm
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 3, intent, 0);//setup the pendingIntent

        //get the method to use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //setExact for kitkat and above
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + left, sender);

        } else {
           //set for jelly bean and bellow
            am.set(AlarmManager.RTC_WAKEUP, left, sender);
        }
        //setup notification
        setupNotif();

        builder.setContentText(getString(R.string.time_left) + appName + "," + futureDate.getTime());//set the notification text

        mgr.notify(notificationId, builder.build());//build the notification
    }

    //warn user that there is a chance that app will not launch (usually happens on kitkat)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(CountdownService.this, R.string.removetask, Toast.LENGTH_LONG).show();

        mgr.notify(notificationId, builder.build());//restart notification
    }

    //method to unregister receivers
    public void removeRecievers(){
        try {
            unregisterReceiver(MyAppReciever);
        } catch(Exception ignored){}

        try {
            unregisterReceiver(receiver2);
        }catch (Exception ignored){}

        try {
            unregisterReceiver(receiver);
        }catch (Exception ignored){}
    }

    //override onDestroy to release everything
    @Override
    public void onDestroy() {
       try {
           ct.cancel();//cancel countdown timer

           mgr.cancel(notificationId);//cancel notification

           stopForeground(true);//stopForeground

           removeRecievers();//remove receivers

           dt = null;//remove the jodaTime Calendar

           stopSelf();//finally stopSelf

       }catch (Exception e){
           stopSelf();
       }
    }

    private static final String DATE_START_ACTION = "DATE_STARTED";
    //broadcast receiver to manage the alarm for the date function
    BroadcastReceiver MyAppReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
               //start app
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                startActivity(launchIntent);

            } catch (Exception e) {
                //if error show toast
                Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }

            //destroy
            onDestroy();
        }
    };
}
package com.daniel.apps.wearapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Daniel on 12/18/2015.
 */
/*launches the selected app after waiting the
* certain amount of time set by the user*/
public class CountdownService extends IntentService {

    private static final String APPLICATION_STOPPED_ACTION = "APPLICATION_STOPPED"; //for the stop countdown button on the notification
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";//for the dismiss notification button on the notification

    String packageName ="";//contains packagename of the selected app
    int Time = 0;//the countdown in milliseconds
    NotificationCompat.Builder builder;//notification builder
    Random ran = new Random();//random generator for notification id
    int notificationId = ran.nextInt();//generates a random id
    boolean cont = true;//boolean to verify if notifications are dismissed or not
    CountDownTimer ct;//countdown timer
    NotificationManagerCompat mgr;//notification manager
    String appName;//holds app label of selected app

    //necessary in a IntentService
    public CountdownService() {
        super("");
    }

    //will handle and coordinate the main functions
    @Override
    protected void onHandleIntent(Intent intent) {
        Notification notify = new Notification();
        notify.when = 0;

        startForeground(notificationId,notify);//to avoid android killing it

        //set up notification dismiss button
        Intent inten = new Intent(NOTIFICATION_DELETED_ACTION);
        registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
        PendingIntent pendintIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, inten, 0);

        //set up notification stop service  button
        Intent inte = new Intent(APPLICATION_STOPPED_ACTION);
        registerReceiver(receiver2, new IntentFilter(APPLICATION_STOPPED_ACTION));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, inte, 0);

        //setup notification
        builder = new NotificationCompat.Builder(getApplicationContext())
        .setContentTitle(getString(R.string.app_name))
        .setSmallIcon(R.mipmap.ic_launcher)
        .addAction(R.drawable.ic_stat_name, getString(R.string.dismiss), pendintIntent)
        .addAction(R.drawable.delete, getString(R.string.stop), pendingIntent);

        mgr = NotificationManagerCompat.from(getApplicationContext());

        //start timer
        ct = new CountDownTimer(Time,1000) {
               @Override
               public void onTick(long millisUntilFinished) {

                   sendNotify(millisUntilFinished,cont);

               }

               @Override
               public void onFinish() {

                   try {

                       Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                       startActivity(launchIntent);

                       sendNotify(0, false);

                       stopSelf();

                   }catch(Exception e){

                       Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_LONG).show();

                       stopSelf();
                   }
               }
           }.start();
    }

    //notification dismiss button onclick
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cont =false;

            unregisterReceiver(receiver2);
            unregisterReceiver(this);
        }
    };

    //notification stop the app launch button onclick
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //stop foreground and remove notification
            ct.cancel();

            ct=null;

            stopSelf();

            unregisterReceiver(receiver);
            unregisterReceiver(this);
        }
    };
    //method to send the notification
    public void sendNotify(long left,boolean cont){

        if(cont) {
          //get time left in proper format
          long second = (left / 1000) % 60;
          long minute = (left / (1000 * 60)) % 60;
          long hour = (left / (1000 * 60 * 60)) % 24;

          String timet = String.format("%02d:%02d:%02d", hour, minute, second);

          //update notification
          builder.setContentText(getString(R.string.launching) + appName + getString(R.string.in)+"\n"+ timet);

          mgr.notify(notificationId, builder.build());

        }else{
          stopForeground(true);

          unregisterReceiver(receiver);
          unregisterReceiver(receiver2);

          mgr.cancel(notificationId);
  }
 }
    //the first method called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Time = intent.getIntExtra("time", 0);
            packageName = intent.getStringExtra("name");
            appName = intent.getStringExtra("appname");

            onHandleIntent(intent);

        }catch (NullPointerException e){
            onHandleIntent(null);
        }

        return START_STICKY;
    }
}

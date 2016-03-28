package com.daniel.apps.appdelay;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Daniel on 1/7/2016.
 */
public class ListActivity extends AppCompatActivity {

    public static final String FIRST = "first";//stores the shared preference for 'firstTime' variable

    ListView list;//will store all the list items
    String packageName;//selected app's packagename
    boolean firstTime = true;//boolean to monitor is the tutorial needs to be launched
    PackageManager pm;//main packageManager to get the app's information
    CustomListViewAdapter adapter;//adapter to hold the list of apps and show them to the user

    //check to see if this is the first time launch this app
    public void checkfirst() {
        //get value from the shared preferences
        firstTime = getSharedPreferences(FIRST, 0).getBoolean("first", firstTime);

       //if firstTime is still false start tutorial
        if (firstTime) {
            startActivity(new Intent(ListActivity.this, MyIntro.class));//start tutorial

            firstTime = false;//set firstTime to false

            //put it into shared preferences so that it doesn't show again
            SharedPreferences prefs = getSharedPreferences(FIRST, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first", firstTime);
            editor.apply();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if tutorial is necessary
        checkfirst();
       //try to setup the support actionbar
        try {
           ActionBar actbar = getSupportActionBar();
            actbar.setTitle(R.string.chooseapp);
            actbar.show();

        }catch (NullPointerException ignored){}
        //list the items
        listit();
    }

    //handle the layout
    public void showList(){
        //setContentView
        setContentView(R.layout.list);

        //instantiate the list
        list = (ListView) findViewById(R.id.listView);

        //add the onClickListener
        addClickListener();

        //set the adapter
        list.setAdapter(adapter);
    }

    //handle the item clicks
    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
                try {
                    //get the packageName
                    packageName = String.valueOf(adapter.getItem(pos).label);

                   //call the next Activity method
                    startit();

                } catch (Exception e) {
                    //error
                    Toast.makeText(ListActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
            }
            }
        });
    }
    //pass the intents and start TImeActivity
    public void startit() {
        //intent to start next Activity
        Intent next = new Intent(this,TimeActivity.class);

        //put the package name as an extra
        next.putExtra("AppName", packageName);

        // set an exit transition for Lollipop and above, because kitkat and bellow have transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());//make it from a certain point view

            //start the activity with the transition
            startActivity(next, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        }else {
            //start the activity
            this.startActivity(next);
        }
    }

    //on separate a thread get all apps and filter them
    public void listit(){
        List<AppDetail> apps = new ArrayList<>();//will contain all the apps installed on device
            pm = getPackageManager();//set the packageManager pm equal to get PackageManager

            //filter through the apps and find the launch-able ones (all the ones found in the app drawer)
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            //get all apps that match the criteria
            List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);

            //cycle through all those apps and the list to add the label packageName and icon to their holders
            for(ResolveInfo ri:availableActivities){
                AppDetail app = new AppDetail();//get the appDetail variables
                app.name = String.valueOf(ri.loadLabel(pm));//set the packagename
                app.icon = ri.activityInfo.loadIcon(pm); //set the icon
                app.label = ri.activityInfo.packageName; //set the label
                apps.add(app);
            }
        //sort the list
        Collections.sort(apps,comp);

        //add them to the adapter
        adapter = new CustomListViewAdapter(getApplicationContext(), R.layout.item, apps);

        //show the list to the user
            showList();
    }

            //sort list method
            Comparator<AppDetail> comp = new Comparator<AppDetail>() {
                public int compare(AppDetail e1, AppDetail e2) {
                    return e1.name.toString().compareToIgnoreCase(e2.name.toString());
                }
            };
}
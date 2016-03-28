package com.daniel.apps.wearapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/*Activity to handle the list of apps layout
and then sends it to TimeActivity*/
public class MainActivity extends Activity {

    ListView list;//will hold list of all launchable apps
    String packageName;//will hold the packagename of selected app
    PackageManager pm;//main packagemanager
    CustomListViewAdapter adapter;//adapter for the list of apps

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_main);

        //watchstub is necessary so that square and round watches get the proper layouts
        final WatchViewStub stub2 = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub2.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //method call to compile and filter the list of apps
                listit();

              //assign packagemanger
                pm = getPackageManager();
            }
        });
        }
    //method to show list
    public void restofApp(){
        //show list
        list = (ListView) findViewById(R.id.listView);

        addClickListener();

        list.setAdapter(adapter);
    }
    //adds onclicklistener to the list
    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                try {
                    packageName = String.valueOf(adapter.getItem(pos).label);

                    startit();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.err, Toast.LENGTH_LONG).show();
            }
            }
        });
    }
    //launch Activity to
    // pick time/date
    public void startit() {
        Intent next = new Intent(this,TimeActivity.class);
        next.putExtra("AppName", packageName);

        this.startActivity(next);
    }

    //on separate thread get list of all apps and filter them
    public void listit(){
        List<AppDetail> apps = new ArrayList<>();

        adapter = new CustomListViewAdapter(getApplicationContext(), R.layout.item, apps);

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);

        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            app.name = String.valueOf(ri.loadLabel(pm));
            app.icon = ri.activityInfo.loadIcon(pm);
            app.label = ri.activityInfo.packageName;

            apps.add(app);
        }

        Collections.sort(apps, comp);

        adapter.sort(comp);

        restofApp();
    }

            //sort list method
            Comparator<AppDetail> comp = new Comparator<AppDetail>() {
                public int compare(AppDetail e1, AppDetail e2) {
                    return e1.toString().compareToIgnoreCase(e2.toString());
                }
            };
}
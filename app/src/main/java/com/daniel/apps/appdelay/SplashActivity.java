package com.daniel.apps.appdelay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Daniel on 2/3/2016.
 */
/*splash screen for the first Activity*/
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //start the first activity
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

        //stop itself
        finish();
    }
}
package com.daniel.apps.appdelay;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Daniel on 10/17/2015.
 */
/*This Activity will only execute the first
 time this app is launched. It shows a quick
 couple slides to inform the user of this app's
 uses and how to use.*/
public class MyIntro extends AppIntro2 {//open source of github


    @Override
    public void init(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        //welcome
        addSlide(AppIntroFragment.newInstance(getString(R.string.welcome), getString(R.string.intro), R.drawable.smallicon, Color.parseColor("#616161")));

        //listActivity
        addSlide(AppIntroFragment.newInstance(getString(R.string.first), getString(R.string.firstdesc), R.drawable.list, Color.parseColor("#616161")));

        //TimeActivity
        addSlide(AppIntroFragment.newInstance(getString(R.string.second),getString(R.string.seconddesc), R.drawable.set_timer, Color.parseColor("#616161")));

        //date function
        addSlide(AppIntroFragment.newInstance(getString(R.string.or),getString(R.string.ordesc), R.drawable.set_date, Color.parseColor("#616161")));

        //notification
        addSlide(AppIntroFragment.newInstance(getString(R.string.notification),getString(R.string.notifdesc), R.drawable.notification, Color.parseColor("#616161")));

    }

    @Override
    public void onDonePressed() {
        //When user taps done, finish activity
        finish();
    }
}
package com.internshipbcc.itrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.internshipbcc.itrip.Fragment.FragmentHome;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if (tabId == R.id.tab_home) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new FragmentHome(), "HOME")
                            .commit();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FragmentHome.VOICE_SEARCH_CODE){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                FragmentHome fHome = (FragmentHome) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);
                fHome.setSearchQuery(result.get(0));
            }
        }
    }
}

package com.example.android.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;

public class MainActivity extends ActionBarActivity {
    public static ActionBar actionBar;
    public static ProgressBar progressBar;
    Bundle bundle;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        bundle = getIntent().getExtras();
        progressBar = (ProgressBar)findViewById(R.id.pbLoading);
        fragment = new SplashScreenFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();


        actionBar = getSupportActionBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
        public void onBackPressed()
        {
            super.onBackPressed();
        }

}

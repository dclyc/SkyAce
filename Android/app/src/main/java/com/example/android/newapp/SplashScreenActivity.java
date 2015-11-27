package com.example.android.newapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by admin on 9/10/15.
 */
public class SplashScreenActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    String user_id = "";
    private CallbackManager callbackManager;

    String[] permission_items;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult loginResult)
        {
            profileTracker = new ProfileTracker()
            {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile profile) {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    i.putExtra("name", profile.getName());
                    i.putExtra("userid", user_id);
                    startActivity(i);
                    finish();
                }
            };

            profileTracker.startTracking();

            accessTokenTracker= new AccessTokenTracker()
            {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                    System.out.println(newToken.getToken());
                    user_id = newToken.getUserId();
                }
            };


            user_id = AccessToken.getCurrentAccessToken().getUserId();
            accessTokenTracker.startTracking();
        }

        @Override
        public void onCancel()
        {

        }

        @Override
        public void onError(FacebookException e)
        {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);
        permission_items = getResources().getStringArray(R.array.facebook_permission_list);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(permission_items);
        loginButton.registerCallback(callbackManager, callback);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("userid","");
        if(!(user_id == null || user_id.equals(""))){
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            i.putExtra("verified", true);
            i.putExtra("userid", user_id);
            startActivity(i);
            finish();
        }else if(AccessToken.getCurrentAccessToken() != null){
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            i.putExtra("verified", true);
            i.putExtra("userid", AccessToken.getCurrentAccessToken().getUserId());
            startActivity(i);
            finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(accessTokenTracker != null)
            accessTokenTracker.stopTracking();
        if(profileTracker != null)
            profileTracker.stopTracking();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

    }

}

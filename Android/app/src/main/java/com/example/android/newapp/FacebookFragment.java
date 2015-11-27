package com.example.android.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by L335A12 on 21/10/15.
 */
/*
public class FacebookFragment extends Fragment
{
    private TextView info;
    private LoginButton loginButton;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

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

                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    if(sharedPreferences != null){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", profile.getName());
                        editor.commit();
                    }
                    displayMessage(profile);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainViewPagerFragment()).addToBackStack(null).commit();

                }
            };

            profileTracker.startTracking();
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("name","");
        if(!(name == null || name.equals(""))){
            Fragment fragment = new OnboardingFragment();

            Bundle bundle = new Bundle();
            bundle.putString("name",name);
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment ).addToBackStack(null).commit();
        }

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };



        accessTokenTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        permission_items = getResources().getStringArray(R.array.facebook_permission_list);
        return inflater.inflate(R.layout.fragment_facebook_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        info = (TextView) view.findViewById(R.id.info);

        loginButton.setReadPermissions(permission_items);
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile)
    {
        if(profile != null){
            info.setText(profile.getName());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        accessTokenTracker.stopTracking();
        if(profileTracker != null)
            profileTracker.stopTracking();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

        displayMessage(profile);
    }

}
*/
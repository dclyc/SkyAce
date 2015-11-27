package com.example.android.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by admin on 14/11/15.
 */
public class SplashScreenFragment extends Fragment {

    private TextView info;
    private LoginButton loginButton;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    String fb_user_id = "";
    private CallbackManager callbackManager;

    String[] permission_items;
    SharedPreferences sharedPref;

    public void commitUserIdAndGoNext(String fb_user_id, String name){

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("fbUserId", fb_user_id);
        editor.putString("name", name);
        editor.commit();

        //Go to other fragment and also pass bundle along
        Fragment fragment = new OnboardingFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fbUserId", fb_user_id);
        bundle.putString("name", name);

        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult loginResult)
        {
            fb_user_id = AccessToken.getCurrentAccessToken().getUserId();
            profileTracker = new ProfileTracker()
            {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile profile) {
                    commitUserIdAndGoNext(profile.getId(), profile.getName());
                }
            };
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    commitUserIdAndGoNext(currentAccessToken.getUserId(), Profile.getCurrentProfile().getName());
                }
            };
            profileTracker.startTracking();
            accessTokenTracker.startTracking();
        }

        @Override
        public void onCancel()
        {

        }

        @Override
        public void onError(FacebookException e)
        {
            System.out.println(e.toString());
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_splash, null);
        permission_items = getResources().getStringArray(R.array.facebook_permission_list);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(permission_items);
        loginButton.registerCallback(callbackManager, callback);

        if(!sharedPref.getString("fbUserId", "").equals("")){
            //already made an attempt to login via facebook
            //let onboarding fragment handle if the user is already registered
            Fragment fragment = new OnboardingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("fbUserId", sharedPref.getString("fbUserId",""));
            bundle.putString("name", sharedPref.getString("name", ""));
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    }
}

package com.example.android.newapp.model;

/**
 * Created by admin on 7/10/15.
 */
public class Session {
    private static boolean loggedIn;
    private static String username;
    private static String accessToken;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Session.loggedIn = loggedIn;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Session.username = username;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        Session.accessToken = accessToken;
    }
}

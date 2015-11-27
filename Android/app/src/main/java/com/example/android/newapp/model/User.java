package com.example.android.newapp.model;

/**
 * Created by L335A12 on 05/10/15.
 */
public class User
{
    String userID;
    String username;
    String email;

    public void setUserID(String id)
    {
        this.userID = id;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUsername(String name)
    {
        this.username = name;
    }

    public String getUsername()
    {
        return username;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }

}

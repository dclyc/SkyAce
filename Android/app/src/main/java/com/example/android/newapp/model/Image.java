package com.example.android.newapp.model;

/**
 * Created by L335A12 on 05/10/15.
 */
public class Image
{
    String imageID;
    String imageContent;
    String filename;
    String fileDirectory;
    String time;
    String date;

    Place p = new Place();

    public Image()
    {

    }

    public Image(String id, String content, String directory)
    {
        this.imageID = id;
        this.imageContent = content;
        this.fileDirectory = directory;
    }

    public Image(String date, String time, double lat, double lng)
    {
        this.date = date;
        this.time = time;
       // p.setGeoTag(lat,lng);
    }

    public void setImageID(String id)
    {
        this.imageID = id;
    }

    public String getImageID()
    {
        return imageID;
    }

    public void setFilename(String file)
    {
        this.filename = file;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFileDirectory(String directory)
    {
        this.fileDirectory = directory;
    }

    public String getFileDirectory()
    {
        return fileDirectory;
    }

    public void setTime(String t)
    {
        this.time = t;
    }

    public String getTime()
    {
        return time;
    }

    public void setDate(String d)
    {
        this.date = d;
    }

    public String getDate()
    {
        return date;
    }




}

package com.example.android.newapp.model;

/**
 * Created by L335A12 on 05/10/15.
 */
public class Place
{
    String _id;
    String name;
    String address;
    Coordinate location;
    String[] tags;
    Photo[] photos;
    Review reviews[];
    boolean isSaved;

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public Review[] getReviews()
    {
        return reviews;
    }





    public void setReviews(Review[] reviews)
    {
        this.reviews = reviews;
    }

    public Photo[] getPhotos()
    {
        return photos;
    }

    public void setPhotos(Photo[] photos)
    {
        this.photos = photos;
    }

    public String[] getTags()
    {
        return tags;
    }

    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String id)
    {
        this._id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Coordinate getLocation()
    {
        return location;
    }

    public void setLocation(Coordinate location)
    {
        this.location = location;
    }



    public class Photo{
        private int height;
        private int width;
        private String photo_reference;

        public int getHeight()
        {
            return height;
        }

        public void setHeight(int height)
        {
            this.height = height;
        }

        public int getWidth()
        {
            return width;
        }

        public void setWidth(int width)
        {
            this.width = width;
        }

        public String getPhoto_reference()
        {
            return photo_reference;
        }

        public void setPhoto_reference(String photo_reference)
        {
            this.photo_reference = photo_reference;
        }
    }
}

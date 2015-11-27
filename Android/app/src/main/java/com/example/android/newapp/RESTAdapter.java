package com.example.android.newapp;

import com.example.android.newapp.model.LenientGsonConverter;
import com.example.android.newapp.model.PhotoByUser;
import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Review;
import com.example.android.newapp.requests.PostReviewForPlace;
import com.example.android.newapp.requests.RegisterUserRequest;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by admin on 7/10/15.
 */
public class RESTAdapter {
    //private static String END_POINT = "http://128.199.255.241:4004";
    //public static String END_POINT = "http://128.199.255.241:4004";
      //public static String END_POINT = "http://172.20.129.184:6002";
    public static String END_POINT = "http://skyace.mybluemix.net";
    //public static String END_POINT = "http://192.168.1.5:6002";
    //public static String END_POINT = "http://192.168.3.95:6002";
    public static RESTInterface getService(){
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        client.setFollowRedirects(true);

        RestAdapter adapter = new RestAdapter.Builder().setClient(new OkClient(client)).setEndpoint(END_POINT).setLogLevel(RestAdapter.LogLevel.FULL).setConverter(new LenientGsonConverter(new Gson())).build();

        RESTInterface service = adapter.create(RESTInterface.class);
        return service;
    }

    public interface RESTInterface{
        @GET("/search")
        void exploreScreen(@Query("access_id") String access_token, @Query("lat") double lat, @Query("lng") double lng, Callback<List<Place>> cb);

        @GET("/check/{userid}")
        void checkUserExists(@Path("userid") String userid, Callback<String> cb);

        @GET("/imageref/{photo}")
        void getImage(@Query("access_id") String access_token, @Path("photo") String image, Callback<String> cb);

        @POST("/register")
        void onboardUser(@Query("access_id")String access_token, @Body RegisterUserRequest userInfo, Callback<String> cb);

        @GET("/place/{placeid}")
        void getPlaceById(@Query("access_id") String access_token, @Path("placeid") String placeid, Callback<Place> cb);

        @GET("/place/photos/{placeid}")
        void getPhotosForPlace(@Query("access_id") String access_token , @Path("placeid") String placeid,  Callback<PhotoByUser[][]> cb);

        @POST("/review/{placeid}")
        void postReviewForPlace(@Query("access_id") String access_token, @Path("placeid") String placeid, @Body PostReviewForPlace place, Callback<Response> cb);

        @GET("/review/explore")
        void getExploringReviews(@Query("access_id") String access_token, Callback<ArrayList<Review>> cb);

        @GET("/save/{placeid}")
        void saveLocation(@Query("access_id") String access_token, @Path("placeid") String placeid,Callback<Response> cb);

        @GET("/saved")
        void getAllSavedLocations(@Query("access_id") String access_token, Callback<List<Place>> cb);

        @GET("/tags")
        void getTags(Callback<String[]> cb);

        @Multipart
        @POST("/place/photo/{placeid}")
        void postPhotoForPlace(@Query("access_id") String access_token, @Path("placeid") String placeid, @Part("image") TypedFile file, Callback<String> cb);

        @GET("/saved/remove/{placeid}")
        void removeSavedPlacesForUser(@Query("access_id") String access_token, @Path("placeid") String placeid, Callback<String> cb);
    }

}

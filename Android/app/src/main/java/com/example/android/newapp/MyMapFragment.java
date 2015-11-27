package com.example.android.newapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newapp.model.Image;
import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.DataFormatException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by L335A12 on 02/10/15.
 */
public class MyMapFragment extends Fragment
{
    private GoogleMap mMap;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    double[] coordinates;
    String name;
    String add;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    String photoFileName;
    String place_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        photoFileName = String.valueOf(System.currentTimeMillis())+".jpg";
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.snap_photo_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        setUpMapIfNeeded();

        Bundle bundle = getArguments();
        place_id = bundle.getString("place_id");
        coordinates = bundle.getDoubleArray("location");
        name = bundle.getString("name");
        add = bundle.getString("add");

        plotMark(coordinates);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode && resultCode == getActivity().RESULT_OK){
            Uri uri = getPhotoFileUri(photoFileName);
            TypedFile typedFile = new TypedFile("image/jpeg", new File(uri.getPath()));
            RESTAdapter.getService().postPhotoForPlace(Session.getAccessToken(), place_id, typedFile, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    if(s.equals("IMAGE_UPLOAD_SUCCESS")){
                        if(getActivity() != null){
                            Toast.makeText(getActivity(), "Image upload successful!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
       }

    }
    public final String APP_TAG = "SkyAce";
    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private void setUpMapIfNeeded()
    {
        if (mMap == null)
        {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

            if (mMap != null)
            {
                setUpMap();
            }
        }
    }

    private void setUpMap()
    {
        mMap.setMyLocationEnabled(true);
    }

    public void plotMark(double[] coor)
    {
        if(mMarker != null)
        {
            mMarker.remove();
        }

        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(coor[1], coor[0])).title(name).snippet(add);
        if(markerOption != null) {
            mMarker = mMap.addMarker(markerOption);
        }
        if(mMap != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coor[1], coor[0]), 15.0f));
        }
    }


}
package com.example.android.newapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.newapp.model.PhotoByUser;
import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Session;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by wei cong on 11/16/2015.
 */
public class PhotosTakenFragment extends Fragment {
    Bundle bundle;
    ListView lvPhotosByDate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_taken, null);
        lvPhotosByDate = (ListView) view.findViewById(R.id.lvPhotosByDate);

        bundle = getArguments();
        if(bundle != null){
            String place_id = bundle.getString("place_id");
            RESTAdapter.getService().getPhotosForPlace(Session.getAccessToken(), place_id, new Callback<PhotoByUser[][]>() {
                @Override
                public void success(PhotoByUser[][] photos, Response response) {
                    lvPhotosByDate.setAdapter(new PhotosAdapter(getActivity(), photos));
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }
        return view;
    }
}

package com.example.android.newapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Session;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin on 9/10/15.
 */
public class SavedLocationsFragment extends Fragment {
    ListView lvSavedLocations;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saved_locations, null);

        return view;
    }
    public void getEntries(){
        if(view == null)
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_saved_locations, null);
        }
        lvSavedLocations = (ListView) view.findViewById(R.id.lvSavedLocations);
        RESTAdapter.getService().getAllSavedLocations(Session.getAccessToken(), new Callback<List<Place>>() {
            @Override
            public void success(final List<Place> response, Response response2) {

                lvSavedLocations.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return response.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return response.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View view = View.inflate(getActivity(), R.layout.layout_saved_location, null);

                        TextView tvPlaceName = (TextView) view.findViewById(R.id.tvPlaceName);
                        TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);

                        final ImageView iv_location_image1 = (ImageView) view.findViewById(R.id.iv_location_image1);
                        Place place = response.get(position);

                        tvPlaceName.setText(place.getName());
                        tvAddress.setText(place.getAddress());
                        String photoUrl;
                        if (place.getPhotos().length > 0)
                        {
                            photoUrl = place.getPhotos()[0].getPhoto_reference();
                            RESTAdapter.getService().getImage(Session.getAccessToken(), photoUrl, new Callback<String>() {
                                @Override
                                public void success(String response, Response response2) {

                                    try {
                                        Bitmap bitmap = new LoadImage().execute(response2.getUrl()).get();
                                        iv_location_image1.setImageBitmap(bitmap);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(SavedLocationsFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                        else
                        {
                            iv_location_image1.setImageDrawable(getResources().getDrawable(R.drawable.photo_not_available));
                        }
                        return view;
                    }
                });
                lvSavedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String place_id = response.get(position).getId();
                        Fragment fragment = new PlacesSpecificFragment();

                        Bundle bundle = new Bundle();

                        bundle.putString("place_id", place_id);
                        fragment.setArguments(bundle);
                        MainActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                        ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getEntries();
        }
    }
}

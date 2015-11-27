package com.example.android.newapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by admin on 7/10/15.
 */
public class SearchFragment extends Fragment
{
    Context context;

    Place place;
    LinearLayout llExploreFeed;
    LocationManager locationManager;
    Location location;
    View view;

    boolean locationFeed = true;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_explore, container, false);
        llExploreFeed = (LinearLayout) view.findViewById(R.id.llExploreFeed);

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);


        if (Build.VERSION_CODES.M == Build.VERSION.SDK_INT && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Toast.makeText(getActivity(), "Unable to get location!", Toast.LENGTH_LONG).show();
            return view;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null)
        {
            Toast.makeText(getActivity(), "Unable to get location! Please check that location is turned on", Toast.LENGTH_LONG).show();
            locationFeed = false;
            return view;
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            getEntries();
        }
    }

    public void getEntries()
    {
/*        if(location == null)
        {
            return ;
        }*/

        //Get feed for explore screen, based on user's information
        RESTAdapter.getService().exploreScreen(Session.getAccessToken(),location.getLatitude(), location.getLongitude(), new Callback<List<Place>>()
        {
            @Override
            public void success(List<Place> places, Response response)
            {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Log.d("places size", String.valueOf(places.size()));

                View view = null;

                for(Place place : places)
                {
                    final Place _place = place;

                    view = inflater.inflate(R.layout.layout_place_single, null);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    view.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Fragment fragment = new PlacesSpecificFragment();

                            Bundle bundle = new Bundle();

                            bundle.putString("place_id", _place.getId());
                            fragment.setArguments(bundle);
                            MainActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                            ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                        }
                    });

                    final ImageButton btnSaveLocation = (ImageButton) view.findViewById(R.id.btnSaveLocation);
                    TextView tvPlaceName = (TextView)view.findViewById(R.id.tvPlaceName);
                    final ImageView ivPlaceImage = (ImageView) view.findViewById(R.id.ivPlaceImage);
                    LinearLayout llPlaceTags = (LinearLayout) view.findViewById(R.id.llPlaceTags);
                    String photoUrl = "";

                    if(_place.isSaved())
                    {
                        btnSaveLocation.setImageDrawable(getResources().getDrawable(R.mipmap.rating));
                    }
                    btnSaveLocation.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(_place.isSaved())
                            {
                                RESTAdapter.getService().removeSavedPlacesForUser(Session.getAccessToken(), _place.getId(), new Callback<String>() {
                                    @Override
                                    public void success(String s, Response response)
                                    {
                                        if(s.equals("DELETED"))
                                        {
                                            Toast.makeText(getActivity(), "Removed from your collection!", Toast.LENGTH_LONG).show();
                                            btnSaveLocation.setImageDrawable(getResources().getDrawable(R.mipmap.unrated));
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                            }else {
                                //Save location to personal "archive"
                                String placeid = _place.getId();
                                RESTAdapter.getService().saveLocation(Session.getAccessToken(), placeid, new Callback<Response>() {
                                    @Override
                                    public void success(Response response, Response response2) {
                                        Toast.makeText(getActivity(), "Saved location", Toast.LENGTH_LONG).show();
                                        btnSaveLocation.setImageDrawable(getResources().getDrawable(R.mipmap.rating));
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

                    if(place.getPhotos().length > 0)
                    {
                        photoUrl = place.getPhotos()[0].getPhoto_reference();
                        RESTAdapter.getService().getImage(Session.getAccessToken(),photoUrl, new Callback<String>()
                        {
                            @Override
                            public void success(String response, Response response2)
                            {

                                try
                                {
                                    Bitmap bitmap = new LoadImage().execute(response2.getUrl()).get();
                                    ivPlaceImage.setImageBitmap(bitmap);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                } catch (ExecutionException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error)
                            {
                                Toast.makeText(SearchFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    else
                    {
                        ivPlaceImage.setImageDrawable(getResources().getDrawable(R.drawable.photo_not_available));
                    }

                    for(String tag : place.getTags())
                    {
                        View customTagTextViewLayout = (View) inflater.inflate(R.layout.layout_custom_tag_textview,null);
                        TextView tvCustomTag = (TextView)customTagTextViewLayout.findViewById(R.id.tvCustomTag);
                        tvCustomTag.setText(tag);
                        //((ViewGroup)customTagTextViewLayout.getParent()).removeView(customTagTextViewLayout);
                        llPlaceTags.addView(customTagTextViewLayout);
                    }
                    tvPlaceName.setText(place.getName());

                    llExploreFeed.addView(view);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

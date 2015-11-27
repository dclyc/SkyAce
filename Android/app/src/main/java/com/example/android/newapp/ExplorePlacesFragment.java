package com.example.android.newapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Review;
import com.example.android.newapp.model.Session;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin on 7/10/15.
 */
public class ExplorePlacesFragment extends Fragment {
    Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        final LinearLayout llExploreFeed = (LinearLayout) view.findViewById(R.id.llExploreFeed);

        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);


        if (Build.VERSION_CODES.M == Build.VERSION.SDK_INT && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Toast.makeText(getActivity(), "Unable to get location!", Toast.LENGTH_LONG).show();
        }

        //Get feed for explore screen, based on user's information
        RESTAdapter.getService().getExploringReviews(Session.getAccessToken(), new Callback<ArrayList<Review>>()
        {
            @Override
            public void success(ArrayList<Review> reviews, Response response) {
                if(getActivity() != null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = null;
                    for (Review review : reviews) {
                        final Place place = review.getPlace();
                        view = inflater.inflate(R.layout.layout_place_with_review_single, null);
                        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragment = new PlacesSpecificFragment();

                                Bundle bundle = new Bundle();

                                bundle.putString("place_id", place.getId());
                                fragment.setArguments(bundle);
                                MainActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                            }
                        });
                        TextView tvPlaceName = (TextView) view.findViewById(R.id.tvPlaceName);
                        final ImageView ivPlaceImage = (ImageView) view.findViewById(R.id.ivPlaceImage);
                        TextView tv_review_title = (TextView) view.findViewById(R.id.tv_review_title);
                        TextView tv_location = (TextView) view.findViewById(R.id.tv_location);
                        LinearLayout llPlaceTags = (LinearLayout) view.findViewById(R.id.llPlaceTags);
                        String photoUrl = "";
                        tv_review_title.setText(review.getUser().getUsername() + " reviewed " + review.getPlace().getName());
                        tv_location.setText(review.getContent());
                        if (place.getPhotos().length > 0) {
                            photoUrl = place.getPhotos()[0].getPhoto_reference();
                            RESTAdapter.getService().getImage(Session.getAccessToken(), photoUrl, new Callback<String>() {
                                @Override
                                public void success(String response, Response response2) {

                                    try {
                                        Bitmap bitmap = new LoadImage().execute(response2.getUrl()).get();
                                        ivPlaceImage.setImageBitmap(bitmap);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(ExplorePlacesFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            ivPlaceImage.setImageDrawable(getResources().getDrawable(R.drawable.photo_not_available));
                        }

                        for (String tag : place.getTags()) {
                            View customTagTextViewLayout = (View) inflater.inflate(R.layout.layout_custom_tag_textview, null);
                            TextView tvCustomTag = (TextView) customTagTextViewLayout.findViewById(R.id.tvCustomTag);
                            tvCustomTag.setText(tag);
                            //((ViewGroup)customTagTextViewLayout.getParent()).removeView(customTagTextViewLayout);
                            llPlaceTags.addView(customTagTextViewLayout);
                        }
                        tvPlaceName.setText(place.getName());

                        llExploreFeed.addView(view);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return view;
    }


}

package com.example.android.newapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newapp.model.Coordinate;
import com.example.android.newapp.model.Place;
import com.example.android.newapp.model.Session;
import com.example.android.newapp.requests.PostReviewForPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin on 7/10/15.
 */
public class PlacesSpecificFragment extends Fragment
{
    String placeid;

    ArrayList<String> results; // -<<-
    Coordinate coordinates; // -<<-
    String naming;
    String add;

    EditText et_review_text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        final Bundle bundle = getArguments();
        placeid = bundle.getString("place_id");
        final ImageButton img_location_picture = (ImageButton) view.findViewById(R.id.img_location_picture);
        final ListView lv_review_list = (ListView) view.findViewById(R.id.lv_review_list);
        final TextView tv_location_text = (TextView) view.findViewById(R.id.tv_location_text); // <<
        final ImageButton btn_submit_review = (ImageButton) view.findViewById(R.id.btn_submit_review); //<<
        final ImageButton btn_voice_translate = (ImageButton) view.findViewById(R.id.btn_voice_translate); // -<<-
        final Button btn_see_photos = (Button) view.findViewById(R.id.btnSeePhotos);
        final Button btn_view_map = (Button) view.findViewById(R.id.btnViewMap);

        btn_see_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PhotosTakenFragment();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });
        btn_view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyMapFragment();

                Bundle bundle = new Bundle();
                bundle.putString("place_id",placeid);
                bundle.putDoubleArray("location", coordinates.getCoordinates());
                bundle.putString("name", naming);
                bundle.putString("add", add);

                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

            }
        });
        et_review_text = (EditText) view.findViewById(R.id.et_review_text);
        final TextView tv_no_reviews_text = (TextView) view.findViewById(R.id.tv_no_reviews_text);

        final ImageButton btn_clear = (ImageButton) view.findViewById(R.id.btn_clear);

        btn_clear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                et_review_text.setText("");
            }
        });

        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostReviewForPlace request = new PostReviewForPlace();
                request.content = et_review_text.getText().toString();
                RESTAdapter.getService().postReviewForPlace(Session.getAccessToken(), placeid, request, new Callback<Response>() {
                    @Override
                    public void success(Response place, Response response) {
                        Fragment fragment = new PlacesSpecificFragment();
                        fragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println(error.getBody());
                    }
                });
            }
        });

        // -<<-
        img_location_picture.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Fragment fragment = new MyMapFragment();

                Bundle bundle = new Bundle();
                bundle.putString("place_id",placeid);
                bundle.putDoubleArray("location", coordinates.getCoordinates());
                bundle.putString("name", naming);
                bundle.putString("add", add);

                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

            }
        });

        // -<<-
        btn_voice_translate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
                startActivityForResult(i, 1010);
            }
        });


        RESTAdapter.getService().getPlaceById(Session.getAccessToken(), placeid, new Callback<Place>()
        {
            @Override
            public void success(final Place response, Response response2)
            {
                tv_location_text.setText(response.getName());
                naming = response.getName();
                add = response.getAddress();

                if (response.getReviews().length == 0)
                {
                    tv_no_reviews_text.setText("No reviews");
                    lv_review_list.setVisibility(View.GONE);
                }
                else
                {
                    tv_no_reviews_text.setVisibility(View.GONE);
                    lv_review_list.setAdapter(new BaseAdapter()
                    {
                        @Override
                        public int getCount()
                        {
                            return response.getReviews().length;
                        }

                        @Override
                        public Object getItem(int position)
                        {
                            return response.getReviews()[position];
                        }

                        @Override
                        public long getItemId(int position)
                        {
                            return position;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent)
                        {
                            ViewHolder holder;
                            if (convertView == null)
                            {
                                holder = new ViewHolder();
                                convertView = View.inflate(PlacesSpecificFragment.this.getContext(), R.layout.layout_review_single, null);
                                holder.content = (TextView) convertView.findViewById(R.id.tv_user_comment);
                                holder.username = (TextView) convertView.findViewById(R.id.tv_name_text);

                                convertView.setTag(holder);
                            }
                            else
                            {
                                holder = (ViewHolder) convertView.getTag();
                            }
                            holder.username.setText(response.getReviews()[position].getUser().getUsername());
                            holder.content.setText(response.getReviews()[position].getContent());
                            return convertView;
                        }

                        class ViewHolder
                        {
                            TextView username;
                            TextView content;
                        }
                    });
                }

                if (response.getPhotos() != null && response.getPhotos().length > 0)
                {
                    RESTAdapter.getService().getImage(Session.getAccessToken(), response.getPhotos()[0].getPhoto_reference(), new Callback<String>()
                    {
                        @Override
                        public void success(String response, Response response2)
                        {

                            try {
                                Bitmap bitmap = new LoadImage().execute(response2.getUrl()).get();
                                img_location_picture.setImageBitmap(bitmap);
                            } catch (InterruptedException e)
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
                            Toast.makeText(PlacesSpecificFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
                else
                {
                    img_location_picture.setImageDrawable(getResources().getDrawable(R.drawable.photo_not_available));
                }

                coordinates = response.getLocation();


            }

            @Override
            public void failure(RetrofitError error)
            {

            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1010 && resultCode == getActivity().RESULT_OK)
        {
            results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


            int index = et_review_text.getSelectionStart();
            et_review_text.setText(et_review_text.getText().insert(index,results.get(0) + " "));

            int len = results.get(0).length();
            et_review_text.setSelection(index + len);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("results", results);
    }

}

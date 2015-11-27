package com.example.android.newapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.newapp.model.Session;
import com.example.android.newapp.requests.RegisterUserRequest;
import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin on 9/10/15.
 */
public class OnboardingFragment extends Fragment
{
    Context context;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    String[] items;
    String fb_user_id;
    List<String> selectedInterests = new ArrayList<String>();
    List<String> tags = new ArrayList<String>();
    boolean[] selectedBool;
    FlowLayout llTags;

    ArrayList<String> selectedItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_onborading, null);
        items = context.getResources().getStringArray(R.array.country_list);
        Button btnSubmitOnboard = (Button) view.findViewById(R.id.btn_submit_onboard);
        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final Spinner spCountry = (Spinner) view.findViewById(R.id.spCountry);
        final Button btnNewInterest = (Button) view.findViewById(R.id.btnNewInterest);
        llTags = (FlowLayout) view.findViewById(R.id.llTags);

        Bundle bundle = getArguments();
        if(bundle != null){
            fb_user_id = bundle.getString("fbUserId");
            String name = bundle.getString("name");
            if(name != null){
                etName.setText(name);
            }
            MainActivity.progressBar.setVisibility(View.VISIBLE);
            MainActivity.progressBar.setIndeterminate(true);
            RESTAdapter.getService().checkUserExists(fb_user_id, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    //Got an access token , so need to redirect to Main Pager
                    if(!s.equals("NOT_A_USER")){
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainViewPagerFragment()).commit();
                        Session.setAccessToken(s);
                    }
                    RESTAdapter.getService().getTags(new Callback<String[]>() {
                        @Override
                        public void success(final String[] strings, Response response) {
                            btnNewInterest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showInputDialog(strings);
                                }
                            });
                            tags.addAll(Arrays.asList(strings));
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                    MainActivity.progressBar.setIndeterminate(false);
                    MainActivity.progressBar.setVisibility(View.GONE);
                }
                @Override
                public void failure(RetrofitError error) {
                    MainActivity.progressBar.setIndeterminate(false);
                    MainActivity.progressBar.setVisibility(View.GONE);
                }
            });
        }

        //If not, is onboarding case
        MainActivity.actionBar.removeAllTabs();
        spCountry.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items));

        btnSubmitOnboard.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                RegisterUserRequest registerUserRequest = new RegisterUserRequest();
                registerUserRequest.country = items[spCountry.getSelectedItemPosition()];
                registerUserRequest.username = etName.getText().toString();
                registerUserRequest.interests = selectedInterests.toArray(new String[selectedInterests.size()]);
                registerUserRequest.userid = fb_user_id;


                RESTAdapter.getService().onboardUser(Session.getAccessToken(), registerUserRequest, new Callback<String>() {
                    @Override
                    public void success(String response, Response response2) {
                        Session.setAccessToken(response);

                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainViewPagerFragment()).commit();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
        spCountry.setSelection(189);
        return view;
    }
    public void showInputDialog(final String[] tags)
    {
        selectedInterests.clear();

        AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Interests");

        if(selectedBool == null)
        {
            selectedBool = new boolean[tags.length];
            for(int i = 0; i < selectedBool.length ; i++)
            {
                selectedBool[i] = false;
            }

            selectedItems = new ArrayList<String>();
        }

        builder.setMultiChoiceItems(tags, selectedBool, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked)
            {
                if (isChecked)
                {
                    // If the user checked the item, add it to the selected items
                    selectedItems.add(tags[indexSelected]);
                }
                else if (selectedItems.contains(tags[indexSelected]))
                {
                    // Else, if the item is already in the array, remove it
                    selectedItems.remove(tags[indexSelected]);
                }
                selectedBool[indexSelected] = isChecked;
            }
        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedInterests.addAll(selectedItems);
                        reloadInterests();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();

    }
    public void reloadInterests()
    {
        if(getActivity() != null)
        {
            llTags.removeAllViews();
            String[] interests = selectedInterests.toArray(new String[]{});

            for(int i = 0; i < interests.length;i++)
            {
                String interest = interests[i];
                final TextView tv = new TextView(getActivity());

                FlowLayout.LayoutParams params= new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 8 , 4, 8);

                tv.setLayoutParams(params);
                tv.setText(interest);
                tv.setClickable(true);
                llTags.addView(tv);
            }
        }
    }
}

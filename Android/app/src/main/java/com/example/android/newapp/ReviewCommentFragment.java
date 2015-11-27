package com.example.android.newapp;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.android.newapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by L335A12 on 09/10/15.
 */
public class ReviewCommentFragment extends ListFragment
{
    ListView list;
    String[] itemname ={"Peter","Jack","Jane","Paul","Andy","Albert","Zack","Leon"};
    Integer[] imgid = {
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar,
            R.mipmap.person_avatar
    };
    String[] itemcomment ={"Llao Llao is good",
            "The serving portion will be as generous as that shown in the picture",
            "Llao Llao is from Spain",
            "With the recently price increase, the servings r getting smaller n smaller",
            "Guys gotta get it",
            "Llao Llao taste so nice",
            "Llao Llao best desert",
            "Llao Llao Watermelon Granillao Best "
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i=0;i<8;i++){
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("txt", "" + itemname[i]);
            hm.put("pic", Integer.toString(imgid[i]));
            hm.put("desc", ""+ itemcomment[i]);
            aList.add(hm);
        }

        String[] from = {"pic", "txt" , "desc"};
        int[] to = {R.id.iv_profile_pic, R.id.tv_username, R.id.tv_userid_comment};

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.list_all, from, to );
        setListAdapter(adapter);
        return inflater.inflate(R.layout.fragment_review, container, false);
    }
}


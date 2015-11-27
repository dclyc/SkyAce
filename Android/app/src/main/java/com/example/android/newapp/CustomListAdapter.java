package com.example.android.newapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by L335A11 on 9/10/2015.x
 */
public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final String[] itemcomment;
    private final Integer[] imgid;

    public CustomListAdapter(Activity context,String[] itemname, Integer[] imgid, String[] itemcomment )
    {
        super(context, R.layout.list_all, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.itemcomment=itemcomment;
    }
    public View getView(int position,View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_all, null, true);

        TextView txtTitle = (TextView)rowView.findViewById(R.id.tv_username);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_profile_pic);
        TextView extratxt = (TextView) rowView.findViewById(R.id.tv_userid_comment);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        extratxt.setText(itemcomment[position]);
        return rowView;
    }
}

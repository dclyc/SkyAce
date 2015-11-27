package com.example.android.newapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.newapp.model.PhotoByUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by wei cong on 11/16/2015.
 */
public class PhotosAdapter extends BaseAdapter {
    Context context;
    PhotoByUser[][] photos;

    public PhotosAdapter(Context context , PhotoByUser[][] photos){
        this.photos = photos;
        this.context = context;
    }
    @Override
    public int getCount() {
        return photos.length;
    }

    @Override
    public Object getItem(int position) {
        return photos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_see_photos_single, null);
        ViewHolder holder;
        if(view.getTag() == null){
            holder = new ViewHolder();
            holder.llPhotos = (LinearLayout)view.findViewById(R.id.llPhotos);
            holder.tvDate = (TextView) view.findViewById(R.id.tvDate);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Date date = new Date((long)photos[position][0].getDate_taken()* 1000);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");

        holder.tvDate.setText(format.format(date));

        ArrayList<ImageView> list = new ArrayList<ImageView>();
        LinearLayout iv = (LinearLayout) inflater.inflate(R.layout.layout_photoimage, null);
        for(int i = 0 ;i < photos[position].length ; i ++){
            FetchImage fi = new FetchImage();
            ImageView imageView = new ImageView(context);


            Drawable d = null;
            try {
                d = fi.execute(photos[position][i].getUrl()).get();
                imageView.setImageDrawable(d);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.weight = (float)4;
                imageView.setLayoutParams(params);

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            list.add(imageView);
        }
        int NUM_PER_ROWS = 3;
        double rowsRounded = Math.ceil(((double) list.size()) /( (double) NUM_PER_ROWS));
        for(int i = 0; i < rowsRounded; i ++){
            LinearLayout ll = new LinearLayout(context);
            ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setWeightSum(12);

            for(int j = 0; j < NUM_PER_ROWS ; j++){
                int index = (i * NUM_PER_ROWS)+j;
                if(index == list.size()){
                    break;
                }
                System.out.println(index);
                ImageView imageView = list.get(index);
                ll.addView(imageView);
            }
            holder.llPhotos.addView(ll);
        }
        return view;
    }
    static class ViewHolder {
        TextView tvDate;
        LinearLayout llPhotos;
    }
    public static int getDPI(int size, DisplayMetrics metrics){
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }
    public class FetchImage extends AsyncTask<String, Void, Drawable>{

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                InputStream is = (InputStream) new URL(params[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "");
                return d;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

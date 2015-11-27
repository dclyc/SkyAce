package com.example.android.newapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newapp.model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by L335A12 on 02/10/15.
 */
public class PhotoFragment extends Fragment
{

    String width;
    String length;

    double lat;
    double lng;

    private Context context;

    Place p;


    TextView tv_photo_info;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Bundle bundle = getArguments();
        String photoName = bundle.getString("photoName");
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_photo);

        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(photoName, options);
        imageView.setImageBitmap(bitmap);

        //int ot = getResources().getConfiguration().orientation;

        tv_photo_info = (TextView)view.findViewById(R.id.tv_photo_info);
        Button btn_submit_location_info = (Button)view.findViewById(R.id.btn_submit_location_info);

        final EditText edit_name = (EditText)view.findViewById(R.id.edit_name);
        final EditText edit_description = (EditText)view.findViewById(R.id.edit_description);

        btn_submit_location_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edit_description.getText().length() == 0 || edit_name.getText().length() ==0 )
                {
                    Toast.makeText(context, "Please fill in the information accordingly ", Toast.LENGTH_SHORT).show();
                    Log.i("error", "here");
                }
                else
                {
                    Log.i("error", "there");
                    String name = edit_name.getText().toString();
                    String descipt = edit_description.getText().toString();

                    //p.getPlacing().add(new Place("1", name, descipt, returnAdd(lat, lng), lat, lng));

                    Bundle bundle = new Bundle();
                    IntroFragment it = new IntroFragment();

                    bundle.putString("name", name);
                    bundle.putString("description", descipt);

                    it.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.frag_intro, it);
                    transaction.addToBackStack(null);

                }

            }
        });


        context = view.getContext();
        tv_photo_info.setText(returnAdd(lat, lng));

        return view;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public String returnAdd(double lat, double lng)
    {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        String address = "";


        try
        {
            List<Address> naming = geoCoder.getFromLocation(lat, lng, 1);

            if(naming != null)
            {
                Address returnedAddress = naming.get(0);
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++)
                {
                    address = returnedAddress.getAddressLine(i);
                }


            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        return address;
    }
}

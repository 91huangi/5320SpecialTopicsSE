package com.cse5320.c2c.crib2castle;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Ivan on 2/24/18.
 */

public class HomeListAdapter extends ArrayAdapter<Home> implements DownloadImageResponse{
    private ArrayList<Home> homeArrayList;
    private Context context;
    private LayoutInflater inflater;
    private int resource;
    private ArrayList<ImageView> dispImages;

    public HomeListAdapter(Context context, int resource, ArrayList<Home> homeArrayList) {
        super(context, com.cse5320.c2c.crib2castle.R.layout.home_item, homeArrayList);
        this.homeArrayList = homeArrayList;
        this.dispImages = new ArrayList<>();
        for(int i=0; i < homeArrayList.size();i++)
            dispImages.add(null);
        this.context = context;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView == null) convertView = inflater.inflate(resource, null);
        TextView tvAddress = (TextView) convertView.findViewById(com.cse5320.c2c.crib2castle.R.id.tvAddress);
        TextView tvBedBaths = (TextView) convertView.findViewById(com.cse5320.c2c.crib2castle.R.id.tvBedBaths);
        TextView tvPrice = (TextView) convertView.findViewById(com.cse5320.c2c.crib2castle.R.id.tvPrice);
        //img = dispImage.get(i);
        dispImages.set(i, (ImageView) convertView.findViewById(com.cse5320.c2c.crib2castle.R.id.ivDispImage));

        Utils u = new Utils();
        Home home = homeArrayList.get(i);
        tvAddress.setText(home.getAddress().toString());
        tvBedBaths.setText(home.getBedrooms()+" Bds | "+home.getBathrooms()+" Bths | "+home.getSqft()+" sqft");
        tvPrice.setText("$"+u.numberToCurrency((long) home.getPrice()));
        loadImages(homeArrayList.get(i), i);
        return convertView;

    }

    public void loadImages(Home home, int index) {

        final int i=index;
        FirebaseStorage storage = FirebaseStorage.getInstance();
            System.out.println("HomeID: "+home.gethId());
            StorageReference homesRef = storage.getReference().child("images/"+home.gethId()+"-"+String.valueOf(0));
            homesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadImage di = new DownloadImage();
                    di.delegate = (DownloadImageResponse) HomeListAdapter.this;
                    di.execute(uri.toString(), String.valueOf(i));
                }
            });
    }

    @Override
    public void downloadImageFinish(Bitmap bitmap, int index) {
        dispImages.get(index).setImageBitmap(bitmap);
        dispImages.get(index).setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

}

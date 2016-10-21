package com.example.tserafin.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.tserafin.movies.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Drawable> mImages;
    private int mImageWidth;
    private int mImageHeight;

    public ImageAdapter(Context c, int width, int height) {
        super();
        mContext = c;
        mImages = new ArrayList<>();
        mImageWidth = width;
        mImageHeight = height;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ImageView getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
//        LayoutInflater inflater = (LayoutInflater) mContext
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
//            imageView = (ImageView) inflater.inflate(R.layout.movie_image_item, null);
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, GridView.AUTO_FIT));

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageDrawable(mImages.get(position));
        return imageView;
    }

    public void clearImages() {
        mImages.clear();
    }

    public void addImage(Drawable image) {
        mImages.add(image);
        notifyDataSetChanged();
    }

    public void setImages(List<Drawable> ids) {
        mImages.clear();
        mImages.addAll(ids);
    }

}

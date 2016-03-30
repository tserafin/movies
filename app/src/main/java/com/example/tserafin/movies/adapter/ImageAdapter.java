package com.example.tserafin.movies.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Drawable> mImages;

    public ImageAdapter(Context c) {
        mContext = c;
        mImages = new ArrayList<>();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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

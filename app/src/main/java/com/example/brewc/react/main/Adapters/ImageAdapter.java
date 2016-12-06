package com.example.brewc.react.main.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.brewc.react.R;
import com.example.brewc.react.main.Utilities.BitmapUtilities;
import com.example.brewc.react.main.Utilities.Photo;

import java.util.List;

/**
 * Custom ArrayAdapter for grid images
 */

public class ImageAdapter extends ArrayAdapter<Photo> {
    private List<Photo> _photos;

    public ImageAdapter(Context context, int textViewResourceId, List<Photo> photos) {
        super(context, textViewResourceId, photos);
        this._photos = photos;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        GridView grid = (GridView)parent;
        int size = grid.getColumnWidth();

        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item_image, null);
            view.setLayoutParams(new GridView.LayoutParams(size, size));
        }

        Bitmap picture = BitmapUtilities.base64ToBitmap(this._photos.get(position).getPhoto());
        ((ImageView) view).setImageBitmap(picture);
        return view;
    }
}
package com.example.petdating;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter<Cards> {
    Context ctx;

    public ArrayAdapter(Context ctx, int resourceId, List<Cards> items) {
        super(ctx, resourceId, items);
    }

    public View getView(int position, View view, ViewGroup parent) {
        Cards card_item = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        name.setText(card_item.getName());
        image.setImageResource(R.mipmap.ic_launcher);

        return view;

    }
}

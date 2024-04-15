package com.example.tugasakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<BagsAdaptedArrayList> {

    private final ArrayList<BagsAdaptedArrayList> people;

    public CustomArrayAdapter(Context context, ArrayList<BagsAdaptedArrayList> people) {
        super(context, 0, people);
        this.people = people;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the layout
        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_button_view_detail, parent, false);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_button_view_detail_element, parent, false);
        }

        // Get the BagsAdaptedArrayList object for the current position
        BagsAdaptedArrayList bagsAdaptedArrayList = getItem(position);

        // Get references to the TextViews in the layout
        TextView nameTextView = convertView.findViewById(R.id.detail_view_bag_id);
        TextView ageTextView = convertView.findViewById(R.id.detail_view_connote_count);

        // Set the text for the name and age TextViews
        nameTextView.setText(bagsAdaptedArrayList.getBagId());
        ageTextView.setText(String.valueOf(bagsAdaptedArrayList.getConnoteCount()));

        return convertView;
    }
}
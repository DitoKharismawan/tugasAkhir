package com.example.tugasakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapterHoBag extends ArrayAdapter<AdaptedArrayList> {
    private final ArrayList<AdaptedArrayList> adaptedArrayLists;
    private final Context context;

    public CustomArrayAdapterHoBag(Context context, ArrayList<AdaptedArrayList> adaptedArrayLists) {
        super(context, 0);
        this.context = context;
        this.adaptedArrayLists = adaptedArrayLists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_bag, parent, false);
        }

        AdaptedArrayList adaptedArrayList = getItem(position);

        TextView bagIdTextView = convertView.findViewById(R.id.bag_id_text_view);
        TextView connoteCountTextView = convertView.findViewById(R.id.connote_count_text_view);
        TextView totalConnoteTextView = convertView.findViewById(R.id.total_connote_text_view);

        bagIdTextView.setText("Bag ID: " + adaptedArrayList.getBagId());
        connoteCountTextView.setText("Connote Count: " + adaptedArrayList.getConnoteCount());
        totalConnoteTextView.setText("Total Connote: " + adaptedArrayList.getTotalConnote());

        return convertView;
    }
}

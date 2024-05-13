package com.example.tugasakhir;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapterHoBag extends ArrayAdapter<AdaptedArrayList> {
 private final   ArrayList<AdaptedArrayList> people;
    private ViewDetailHoBag ctx;



    public CustomArrayAdapterHoBag(ViewDetailHoBag context, ArrayList<AdaptedArrayList>people) {
        super(context, 0, people);
        this.ctx = context;
        this.people = people;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bag, parent, false);
        }

        AdaptedArrayList adaptedArrayElement = getItem(position);

        TextView bagIdTextView = convertView.findViewById(R.id.bag_id_text_view);
        TextView connoteCountTextView = convertView.findViewById(R.id.connote_count_text_view);
        TextView totalConnoteTextView = convertView.findViewById(R.id.total_connote_text_view);
        Button btnDelete = convertView.findViewById(R.id.detail_view_btn_delete);

        bagIdTextView.setText("Bag ID: " + adaptedArrayElement.getBagId());
        connoteCountTextView.setText("Connote Count: " + adaptedArrayElement.getConnoteCount());
        totalConnoteTextView.setText("Total Connote: " + adaptedArrayElement.getTotalConnote());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBag(adaptedArrayElement.getBagId(), position);
            }
        });

        return convertView;
    }

    private void deleteBag(String bagCtx, Integer arrayIndex) {
        ((ViewDetailHoBag)ctx).deleteBagHookHBag(bagCtx, arrayIndex);
    }
}
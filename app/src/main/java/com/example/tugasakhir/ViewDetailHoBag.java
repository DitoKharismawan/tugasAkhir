package com.example.tugasakhir;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDetailHoBag extends AppCompatActivity {
    ListView listViewScannedResultsHoBag;
    ArrayList<String> scannedResultsHoBag;
    TugasAkhirContext taCtx;
    CustomArrayAdapterHoBag listAdapter;
    ArrayList<String> dataList = new ArrayList<>();
    ArrayList<AdaptedArrayList> adaptedDataList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_ho_bag);

        listViewScannedResultsHoBag = findViewById(R.id.listViewScannedResultsHoBag);

        // Retrieve scanned results from Intent (assuming key is "scannedResults")
        scannedResultsHoBag = getIntent().getStringArrayListExtra("scannedResults");

        if (scannedResultsHoBag != null && !scannedResultsHoBag.isEmpty()) {
            // Initialize ArrayList to hold data from Firebase


            // Iterate through scanned results
            for (String scanResult : scannedResultsHoBag) {
                // Get reference to "bagCtx" node in Firebase and find the bag with matching scanResult
                FirebaseDatabase.getInstance().getReference().child("bags").child(scanResult)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Get data as a map
                                    Map<String, Object> bagData = (Map<String, Object>) dataSnapshot.getValue();
                                    if (bagData != null) {
                                        // Extract required information from the map
                                        Long totalConnote = (Long) bagData.get("totalConnote");
                                        Integer totalConnoteInt = totalConnote.intValue();
                                        String bagInfo = "Bag ID: " + scanResult + "Total Connote" + totalConnote;
                                        AdaptedArrayList adaptedElement = new AdaptedArrayList(scanResult, totalConnoteInt, totalConnoteInt);
                                        dataList.add(bagInfo);
                                        adaptedDataList.add(adaptedElement);
                                        Log.d("App", "Creating arrayAdapter:" + adaptedDataList.size());

                                        // Update ListView with the new data
//                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewDetailHoBag.this, android.R.layout.simple_list_item_1, dataList);
                                        ArrayAdapter<AdaptedArrayList> adapter = new CustomArrayAdapterHoBag(ViewDetailHoBag.this, adaptedDataList);
                                        listAdapter = (CustomArrayAdapterHoBag) adapter;
                                        listViewScannedResultsHoBag.setAdapter(adapter);
                                    }
                                } else {
                                    Toast.makeText(ViewDetailHoBag.this, "No data found for scanned result: " + scanResult, Toast.LENGTH_SHORT).show();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ViewDetailHoBag.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            // Show message if no scanned results available
            Toast.makeText(this, "No scanned data available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adaptedDataList.isEmpty() && listAdapter != null) {
            scannedResultsHoBag = null;
            adaptedDataList.clear();
            dataList.clear();
            listAdapter.clear();
        }
    }

    public void deleteBagHookHBag(String bagCtx, Integer arrayIndex) {
        Toast.makeText(getApplicationContext(), "[Direct] : " + bagCtx + " [By Idx] : " + adaptedDataList.get(arrayIndex).getBagId(), Toast.LENGTH_SHORT).show();
        adaptedDataList.remove(arrayIndex);
        dataList.remove(arrayIndex); // Remove corresponding item from dataList as well
        listAdapter.remove(adaptedDataList.get(arrayIndex)); // Update adapter

        // **New line to clear scannedResultsHoBag**
        if (scannedResultsHoBag != null && !scannedResultsHoBag.isEmpty() && arrayIndex < scannedResultsHoBag.size()) {
            scannedResultsHoBag.remove(arrayIndex.intValue());  // Use arrayIndex to remove the element
        }
    }
}
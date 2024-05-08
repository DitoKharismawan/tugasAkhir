package com.example.tugasakhir;

import android.os.Bundle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_ho_bag);

        listViewScannedResultsHoBag = findViewById(R.id.listViewScannedResultsHoBag);

        // Retrieve scanned results from Intent (assuming key is "scannedResults")
        scannedResultsHoBag = getIntent().getStringArrayListExtra("scannedResults");

        if (scannedResultsHoBag != null && !scannedResultsHoBag.isEmpty()) {
            // Initialize ArrayList to hold data from Firebase
            ArrayList<String> dataList = new ArrayList<>();

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
                                        String totalConnote = bagData.get("totalConnote").toString();
                                        String bagInfo = "Bag ID: " + scanResult + "Total Connote"+totalConnote ;
                                        dataList.add(bagInfo);

                                        // Update ListView with the new data
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewDetailHoBag.this, android.R.layout.simple_list_item_1, dataList);
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
}

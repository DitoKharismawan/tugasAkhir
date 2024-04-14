package com.example.tugasakhir;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Map;

public class DetailConnoteActivity extends AppCompatActivity {
    ListView listViewDetail;
    ArrayList<String> detailList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_connote);

        listViewDetail = findViewById(R.id.listViewDetailConnote);
        detailList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailList);
        listViewDetail.setAdapter(adapter);

        // Ambil hasil pemindaian dari intent
        String scannedData = getIntent().getStringExtra("selectedItem");
        if (scannedData != null) {
            FirebaseDatabase.getInstance().getReference().child("awb").child(scannedData)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Periksa apakah snapshot data eksis
                            if (dataSnapshot.exists()) {
                                // Data dapat berupa single value atau Map jika memiliki child
                                if (dataSnapshot.getValue() instanceof Map) {
                                    // Cast snapshot menjadi Map untuk mengakses data
                                    Map<String, Object> dataMap = (Map<String, Object>) dataSnapshot.getValue();
                                    // Ambil data yang diperlukan
                                    String detail = "AWB: " + dataSnapshot.getKey();
                                    if (dataMap != null) {
                                        if (dataMap.get("awb") != null)
                                            detail += ", AWB: " + dataMap.get("awb");
                                        if (dataMap.get("qty") != null)
                                            detail += ", Qty: " + dataMap.get("qty");
                                        if (dataMap.get("weight") != null)
                                            detail += ", Weight: " + dataMap.get("weight");
                                    }
                                    // Tambahkan detail ke list dan beritahu adapter tentang perubahan data
                                    detailList.add(detail);
                                } else {
                                    // Jika data bukan Map, ambil value langsung
                                    String detail = dataSnapshot.getValue(String.class);
                                    detailList.add("Data: " + detail);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                // Handle jika tidak ada data atau tidak ditemukan
                                detailList.add("Data tidak ditemukan untuk AWB: " + scannedData);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log error atau tampilkan pesan error
                            Log.e("DetailConnoteActivity", "Error loading data", databaseError.toException());
                        }
                    });
        }
    }
}
package com.example.tugasakhir;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    ArrayList<String> connoteContents;
    ArrayAdapter<String> adapter;

    TugasAkhirContext taCtx;
    BagDataStore bagCtx;
    GlobalConstant gConstant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_connote);

        taCtx = (TugasAkhirContext) getApplicationContext();
        bagCtx = taCtx.getBagDataStore();
        gConstant = taCtx.getConstant();

        listViewDetail = findViewById(R.id.listViewDetailConnote);
        detailList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailList);
        listViewDetail.setAdapter(adapter);

        // Ambil hasil pemindaian dari intent
        String selectedBag = getIntent().getStringExtra("selectedItem");
        connoteContents = getIntent().getStringArrayListExtra("connoteContents");
        Toast.makeText(this, "Mengambil " + connoteContents.size() + " connote...", Toast.LENGTH_SHORT).show();
        for ( String connote : connoteContents ) {
            appendBag(connote);
        }

        listViewDetail.setOnItemClickListener((parent, view, position, id) -> {
            ;
//                String selectedItem = scannedResults.get(position);
//                String selectedItem = scannedResults.get(position);
            // Pindah ke DetailConnoteActivity dengan membawa data terpilih (contoh: nomor AWB)
//                navigateToDetailConnoteActivity(selectedItem);
//            BagsAdaptedArrayList selectedBag = bagsAdaptedList.get(position);
//            ArrayList<String> connoteContents = bagsHolder.get(selectedBag.getBagId());
            deleteConnote(position);

        });
    }

    private void appendBag(String selectedBag) {
        if (selectedBag != null) {
            FirebaseDatabase.getInstance().getReference().child("awb").child(selectedBag)
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
                                detailList.add("Data tidak ditemukan untuk AWB: " + selectedBag);
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

    private void deleteConnote(Integer connoteIdx) {
        // Ini perlu diupdate lgi kedepannya
        String connoteId = connoteContents.get(connoteIdx);
        String bagName = bagCtx.getConnoteBag(connoteId);
        showConfirmDialog(
                "Hapus Connote",
                "Yakin Hapus Connote " + connoteId + "?" +
                        (bagName != null ? "" : "\n(Tetapi connote ini tidak ada di bag manapun)"))
                .setPositiveButton(
                        "Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (bagCtx.smartRemoveConnote(connoteId)) {
                                    Toast.makeText(getApplicationContext(), "Terhapus Connote  " + connoteId, Toast.LENGTH_LONG).show();
                                    adapter.remove(detailList.get(connoteIdx));
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Connote " + connoteId + " tidak ada di bag manapun", Toast.LENGTH_LONG).show();
                                };
                            }
                        })
                .setNegativeButton(
                        "Batal",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do Something Here
                                Toast.makeText(getApplicationContext(), "Aksi dibatalkan", Toast.LENGTH_LONG).show();
                            }
                        })
                .show();
    }

    private AlertDialog.Builder showConfirmDialog(String judul, String pesan) {
        return new AlertDialog.Builder(this)
                .setTitle(judul)
                .setMessage(
                        pesan)
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert));
    }

}


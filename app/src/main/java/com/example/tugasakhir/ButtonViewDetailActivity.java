package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ButtonViewDetailActivity extends AppCompatActivity {
    ListView listViewScannedResultsBag;
    ArrayList<BagsAdaptedArrayList> bagsAdaptedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view_detail);

        listViewScannedResultsBag = findViewById(R.id.listViewScannedResultsBag);
        HashMap<String, ArrayList<String>> bagsHolder = (HashMap<String, ArrayList<String>>) getIntent().getSerializableExtra("bagsHolder");
        for (String bagName : bagsHolder.keySet()) {
            ArrayList<String> connoteList = bagsHolder.get(bagName);
            bagsAdaptedList.add(new BagsAdaptedArrayList(bagName, connoteList.size()));
        }

        // Mendapatkan hasil scan dari Intent
//        ArrayList<String> scannedResults = getIntent().getStringArrayListExtra("scannedResults");
        ArrayList<String> scannedResults = getIntent().getStringArrayListExtra("scansBag");
        Toast.makeText(this, "[SCN DETAIL] " + scannedResults.toString(), Toast.LENGTH_SHORT).show();

        // Memastikan hasil scan tidak null dan tidak kosong
        if (scannedResults != null && !scannedResults.isEmpty()) {
            // Menggunakan ArrayAdapter untuk menampilkan data dalam ListView
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedResults);
            CustomArrayAdapter adapter = new CustomArrayAdapter(getApplicationContext(), bagsAdaptedList);
            listViewScannedResultsBag.setAdapter(adapter);

            // Menambahkan onClickListener pada item ListView
            listViewScannedResultsBag.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedItem = scannedResults.get(position);
//                String selectedItem = scannedResults.get(position);
                // Pindah ke DetailConnoteActivity dengan membawa data terpilih (contoh: nomor AWB)
//                navigateToDetailConnoteActivity(selectedItem);
                BagsAdaptedArrayList selectedBag = bagsAdaptedList.get(position);
                ArrayList<String> connoteContents = bagsHolder.get(selectedBag.getBagId());
                navigateToDetailConnoteActivity(selectedBag.getBagId(), connoteContents);
            });
        } else {
            Toast.makeText(this, "No scanned data available.", Toast.LENGTH_SHORT).show();
        }
    }

//    private void navigateToDetailConnoteActivity(String selectedItem) {
    private void navigateToDetailConnoteActivity(String nomorBag, ArrayList<String> connoteContents) {
        // Di sini Anda dapat membuat Intent dan memulai DetailConnoteActivity,
        // serta mengirim data terpilih menggunakan Intent (misalnya, nomor AWB).

        Intent intent = new Intent(ButtonViewDetailActivity.this, DetailConnoteActivity.class);
        intent.putExtra("selectedItem", nomorBag);
        intent.putStringArrayListExtra("connoteContents", connoteContents);
        startActivity(intent);

    }
}


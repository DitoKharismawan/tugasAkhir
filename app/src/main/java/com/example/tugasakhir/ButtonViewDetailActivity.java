package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ButtonViewDetailActivity extends AppCompatActivity {
    ListView listViewScannedResultsBag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view_detail);

        listViewScannedResultsBag = findViewById(R.id.listViewScannedResultsBag);

        // Mendapatkan hasil scan dari Intent
        ArrayList<String> scannedResults = getIntent().getStringArrayListExtra("scannedResults");

        // Memastikan hasil scan tidak null dan tidak kosong
        if (scannedResults != null && !scannedResults.isEmpty()) {
            // Menggunakan ArrayAdapter untuk menampilkan data dalam ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedResults);
            listViewScannedResultsBag.setAdapter(adapter);

            // Menambahkan onClickListener pada item ListView
            listViewScannedResultsBag.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = scannedResults.get(position);
                // Pindah ke DetailConnoteActivity dengan membawa data terpilih (contoh: nomor AWB)
                navigateToDetailConnoteActivity(selectedItem);
            });
        } else {
            Toast.makeText(this, "No scanned data available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDetailConnoteActivity(String selectedItem) {
        // Di sini Anda dapat membuat Intent dan memulai DetailConnoteActivity,
        // serta mengirim data terpilih menggunakan Intent (misalnya, nomor AWB).

        Intent intent = new Intent(ButtonViewDetailActivity.this, DetailConnoteActivity.class);
        intent.putExtra("selectedItem", selectedItem);
        startActivity(intent);

    }
}

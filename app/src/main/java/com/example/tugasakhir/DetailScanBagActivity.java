package com.example.tugasakhir;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DetailScanBagActivity extends AppCompatActivity {
    private ListView listViewScannedResultsScanBag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_scan_bag);
        ArrayList<String> scannedResults = getIntent().getStringArrayListExtra("scannedResults");
        if (scannedResults != null && !scannedResults.isEmpty()) {
            listViewScannedResultsScanBag = findViewById(R.id.listViewScannedResultsScanBag); // Replace with your actual ListView ID
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, scannedResults);
            listViewScannedResultsScanBag.setAdapter(adapter);
        } else {
            // No scanned results found
            Toast.makeText(this, "No scanned data received", Toast.LENGTH_SHORT).show();
        }
    }
}


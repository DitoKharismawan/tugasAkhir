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

public class DetailOutstandingActivity extends AppCompatActivity {

    private ListView listViewScannedResultsOutBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_outstanding);

        // Get the scanned results ArrayList from the Intent extra
        ArrayList<String> scannedResults = getIntent().getStringArrayListExtra("scannedResults");

        // Check if scanned results are available
        if (scannedResults != null && !scannedResults.isEmpty()) {
            // Display the scanned data in a ListView
            listViewScannedResultsOutBag = findViewById(R.id.listViewScannedResultsOutBag); // Replace with your actual ListView ID

            // Create a new ArrayAdapter to populate the ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, scannedResults);
            listViewScannedResultsOutBag.setAdapter(adapter);
        } else {
            // No scanned results found
            Toast.makeText(this, "No scanned data received", Toast.LENGTH_SHORT).show();
        }
    }
}


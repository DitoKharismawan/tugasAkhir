package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceivingBagActivity extends AppCompatActivity {
    ImageButton scanButtonSearchBag;
    BagDataStore bagCtx;
    TextView elmIncBag;
    private Intent data;
    ListView listViewScannedResultsOutBag;
    Button buttonDetailOutBag;
    ArrayList<String> gScannedResultsHoBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_bag);

        scanButtonSearchBag = findViewById(R.id.scanButtonSearchBag);
        buttonDetailOutBag = findViewById(R.id.buttonDetailOutBag);
        elmIncBag = findViewById(R.id.textViewIncrementOutBag);

        // Initialize empty array list to store scanned results
        gScannedResultsHoBag = new ArrayList<>();

        buttonDetailOutBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there are scanned results before starting the DetailOutstandingActivity


                // Create an Intent to start DetailOutstandingActivity
                Intent intent = new Intent(ReceivingBagActivity.this, DetailOutstandingActivity.class);
                intent.putExtra("scannedResults", gScannedResultsHoBag);
                startActivity(intent);
            }
        });

        scanButtonSearchBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScanner();
            }
        });
    }

    private void startBarcodeScanner() {
        // Inisialisasi IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(ReceivingBagActivity.this);
        integrator.setRequestCode(50004);
        // Konfigurasi untuk IntentIntegrator
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
        integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
        integrator.initiateScan(); // Memulai pemindaian
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle scan results if the request code matches
        if (requestCode == 50004) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(resultCode, data);
            if (scanResult != null) {
                if (scanResult.getContents() != null) {
                    // Add the scanned barcode to the array list
                    gScannedResultsHoBag.add(scanResult.getContents());
                    Log.d("Scanned Result", scanResult.getContents());
                    // Firebase Realtime Database reference
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("gScannedResultsHoBag");

                    // Check if scanned code matches a record in Firebase
                    databaseReference.child(scanResult.getContents())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Matched data found in Firebase
                                        // You can potentially populate additional information about the scan result here
                                        // based on the data retrieved from Firebase
                                    } else {
                                        // No matching data found in Firebase
                                        // Handle this scenario as needed (e.g., display a message to the user)
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    // Toast message to indicate successful scan
                    Toast.makeText(this, "Barcode scanned successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle scan cancellation
                    Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

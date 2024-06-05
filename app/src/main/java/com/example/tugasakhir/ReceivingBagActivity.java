package com.example.tugasakhir;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ReceivingBagActivity extends AppCompatActivity {
    ImageButton scanButtonSearchBag, scanButtonRcvBag;
    BagDataStore bagCtx;
    TextView elmIncBag,elmIncRcvBag;

    Button buttonDetailOutBag, buttonDetailScanBag;
    private static ArrayList<String> gScannedResultsHoBag = new ArrayList<>();
    private static ArrayList<String> scannedResultsRcvBag = new ArrayList<>();
    int totalItems = 0;
    private int totalScannedItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_bag);
        scanButtonRcvBag = findViewById(R.id.scanButtonRcvBag);
        scanButtonSearchBag = findViewById(R.id.scanButtonSearchBag);
        buttonDetailOutBag = findViewById(R.id.buttonDetailOutBag);
        elmIncBag = findViewById(R.id.textViewIncrementOutBag); // Fix the typo (double dot)
        elmIncRcvBag=findViewById(R.id.textViewIncrementScanBag);
        buttonDetailScanBag = findViewById(R.id.buttonDetailScanBag);
        // Initialize empty array list to store scanned results
        gScannedResultsHoBag = new ArrayList<>();

        // Update elmIncBag text to reflect the initial size of the array
        updateScannedResultCount();

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
        buttonDetailScanBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there are scanned results before starting the DetailOutstandingActivity


                // Create an Intent to start DetailOutstandingActivity
                Intent intent = new Intent(ReceivingBagActivity.this, DetailScanBagActivity.class);
                intent.putExtra("scannedResults",scannedResultsRcvBag);
                startActivity(intent);
            }
        });
        scanButtonRcvBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScannerRcvBag();
            }
        });
        scanButtonSearchBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScanner();
            }
        });
    }

    private void updateScannedResultCount() {
        // Update the text view to display the current number of scanned results
        elmIncBag.setText(String.valueOf(gScannedResultsHoBag.size()));
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

    private void startBarcodeScannerRcvBag() {

        // Inisialisasi IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(ReceivingBagActivity.this);
        integrator.setRequestCode(50005);
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
                    // Add the scanned barcode to the array list (optional)
                    // gScannedResultsHoBag.add(scanResult.getContents());
                    Log.d("Scanned Result", scanResult.getContents());

                    // Firebase Realtime Database reference
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bagsToHo");

                    // Look up data based on the scanned barcode (assuming it's a bag index)
                    databaseReference.child(scanResult.getContents()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Matched data found for the scanned barcode (bag index)
                                String hoId = dataSnapshot.child("HoId").getValue(String.class);

                                // Query bagsToHo for all data with the same HoId
                                Query query = databaseReference.orderByChild("HoId").equalTo(hoId);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Loop through all child nodes under "bagsToHo" with the matching HoId
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                // Extract bag index (assuming childSnapshot.getKey() is the bag index)
                                                String bagIndex = childSnapshot.getKey();
                                                totalItems++;
                                                // Add bag index or other relevant data to an ArrayList
                                                gScannedResultsHoBag.add(bagIndex);
                                            }
                                            elmIncBag.setText(String.valueOf(totalItems));
                                        } else {
                                            // No data found with the matching HoId
                                            // Handle this scenario as needed (e.g., display a message to the user)
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                // No data found for the scanned barcode (bag index)
                                // Handle this scenario as needed (e.g., display a message to the user)
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // Toast message can be modified to indicate successful scan and data lookup
                    Toast.makeText(this, "Scanned and data retrieved successfully", LENGTH_SHORT).show();
                } else {
                    // Handle scan cancellation
                    Toast.makeText(this, "Scan canceled", LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == 50005) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(resultCode, data);
            if (scanResult != null) {
                if (scanResult.getContents() != null) {
                    String scannedData = scanResult.getContents();

                    // Add scanned data to detail scan results list
                    scannedResultsRcvBag.add(scannedData);
                    Log.d("Scanned Result (Detail)", scannedData);
                    int detailScanCount = scannedResultsRcvBag.size();
                    elmIncRcvBag.setText(String.valueOf(detailScanCount));
                    // Check if the scanned data exists in the main scan results (gScannedResultsHoBag)
                    int indexToRemove = -1;
                    for (int i = 0; i < gScannedResultsHoBag.size(); i++) {
                        if (gScannedResultsHoBag.get(i).equals(scannedData)) {
                            indexToRemove = i;
                            break;
                        }
                    }

                    // Remove the matching data from the main scan results if found
                    if (indexToRemove != -1) {
                        gScannedResultsHoBag.remove(indexToRemove);
                        totalItems--; // Update total scan count (optional)
                        elmIncBag.setText(String.valueOf(totalItems)); // Update UI (optional)
                        Log.d("Scanned Result (Detail)", "Matching data removed from main scan results");
                    }

                    // You can add further processing here if needed for detail scan results
                    // ... (your additional logic for handling detail scan data)
                } else {
                    // Handle scan cancellation
                    Toast.makeText(this, "Scan canceled", LENGTH_SHORT).show();
                }
            }
        }
    }
}


package com.example.tugasakhir;

import static android.widget.Toast.LENGTH_SHORT;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceivingBagActivity extends AppCompatActivity {
    ImageButton scanButtonSearchBag,scanButtonRcvBag;
    BagDataStore bagCtx;
    TextView elmIncBag;
    private Intent data;
    ListView listViewScannedResultsOutBag;
    Button buttonDetailOutBag;
    ArrayList<String> gScannedResultsHoBag;
    int totalItems = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_bag);
        scanButtonRcvBag = findViewById(R.id.scanButtonRcvBag);
        scanButtonSearchBag = findViewById(R.id.scanButtonSearchBag);
        buttonDetailOutBag = findViewById(R.id.buttonDetailOutBag);
        elmIncBag = findViewById(R.id.textViewIncrementOutBag); // Fix the typo (double dot)

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
                                            } elmIncBag.setText(String.valueOf(totalItems));
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
        }
    }

}

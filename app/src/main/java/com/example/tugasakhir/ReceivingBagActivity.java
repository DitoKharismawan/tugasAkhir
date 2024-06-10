package com.example.tugasakhir;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceivingBagActivity extends AppCompatActivity {
    ImageButton scanButtonSearchBag, scanButtonRcvBag;
    ImageView backButton;
    BagDataStore bagCtx;
    TextView elmIncBag,elmIncRcvBag;
    EditText editTextRcvBagNo,editTextTanggalRcv,editTextUserRcv,editTextRemarksRcv,editTextSearchBag,editTextScanRcvBag;
    Button buttonDetailOutBag, buttonDetailScanBag,buttonCreateRcvBag,approveButtonRcvBag;
    private static ArrayList<String> gScannedResultsHoBag = new ArrayList<>();
    private static ArrayList<String> scannedResultsRcvBag = new ArrayList<>();
    int totalItems = 0;
    private int totalScannedItems;
    private int rcvBagCounter = 0;
    private final String bagPrefix = "CGK_RCVB_";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_bag);
        backButton=findViewById(R.id.backButton);
        editTextRcvBagNo=findViewById(R.id.editTextRcvBagNo);
        editTextTanggalRcv=findViewById(R.id.editTextTanggalRcv);
        editTextUserRcv=findViewById(R.id.editTextUserRcv);
        editTextRemarksRcv=findViewById(R.id.editTextRemarksRcv);
        editTextSearchBag=findViewById(R.id.editTextSearchBag);
        editTextScanRcvBag=findViewById(R.id.editTextScanRcvBag);
        scanButtonRcvBag = findViewById(R.id.scanButtonRcvBag);
        scanButtonSearchBag = findViewById(R.id.scanButtonSearchBag);
        buttonDetailOutBag = findViewById(R.id.buttonDetailOutBag);
        elmIncBag = findViewById(R.id.textViewIncrementOutBag); // Fix the typo (double dot)
        elmIncRcvBag=findViewById(R.id.textViewIncrementScanBag);
        buttonDetailScanBag = findViewById(R.id.buttonDetailScanBag);
        buttonCreateRcvBag=findViewById(R.id.buttonCreateRcvBag);
        approveButtonRcvBag=findViewById(R.id.approveButtonRcvBag);
        // Initialize empty array list to store scanned results
        gScannedResultsHoBag = new ArrayList<>();

        // Update elmIncBag text to reflect the initial size of the array
        updateScannedResultCount();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ReceivingBagActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
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
        approveButtonRcvBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApproveButtonClick();
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
        buttonCreateRcvBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }



            private void fillEditTextWithAutoIncrementAndTimestamp() {
                // Retrieve the saved counter value from SharedPreferences (if any)
                int savedRcvBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("rcvBagCounter", 0);

                // Increment the counter based on the saved value
                int rcvBagCounter = savedRcvBagCounter + 1;

                // Update the SharedPreferences with the new counter value
                getSharedPreferences("app_data", MODE_PRIVATE)
                        .edit()
                        .putInt("rcvBagCounter", rcvBagCounter)
                        .apply();


                editTextRcvBagNo.setText(String.valueOf(bagPrefix + rcvBagCounter));

                // Mengatur tanggal dengan format timestamp
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalRcv.setText(currentDate);

                // EditText untuk Nama User dan Origin akan diisi di method fetchUserData()
            }
        });
    }

    private void handleApproveButtonClick() {
        // Validate user input before proceeding
        if (editTextRcvBagNo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Receiving Bag No.", LENGTH_SHORT).show();
            return;
        }

        if (editTextTanggalRcv.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Receiving Date.", LENGTH_SHORT).show();
            return;
        }

        // Assuming BagRcvData class represents the data structure for receiving bags
        String bagKey = editTextRcvBagNo.getText().toString(); // Use editTextRcvBagNo for bagKey

        BagRcvData newBagData = new BagRcvData(
                bagKey,  // Use bagKey here
                editTextUserRcv.getText().toString(),
                editTextTanggalRcv.getText().toString(),
                scannedResultsRcvBag
        );

        // Save data to Firebase Realtime Database
        DatabaseReference receivingBagsRef = FirebaseDatabase.getInstance().getReference().child("receivingBags");

        receivingBagsRef.child(bagKey).setValue(newBagData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data saved successfully
                        Toast.makeText(getApplicationContext(), "Receiving Bag data saved successfully", Toast.LENGTH_SHORT).show();

                        // Clear UI elements after successful save (optional)
                        clearUiAfterSave();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors during data saving
                        Toast.makeText(getApplicationContext(), "Failed to save Receiving Bag data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearUiAfterSave() {
        editTextRcvBagNo.setText("");
        editTextTanggalRcv.setText("");
        editTextUserRcv.setText(""); // Assuming user doesn't need to be reset
        editTextRemarksRcv.setText("");
        editTextSearchBag.setText("");
        editTextScanRcvBag.setText("");
        elmIncRcvBag.setText("0");
        elmIncBag.setText("0");
        // ... (clear other relevant UI elements if needed)
    }

    private void fetchUserData() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming you want to fetch the data of the first user found
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming 'username' and 'origin' are child nodes under each user
                        String username = userSnapshot.child("username").getValue(String.class);
                        editTextUserRcv.setText(username != null ? username : "User");

                        // Stop after fetching the first user's data
                        break;
                    }
                } else {
                    Log.d("FirebaseData", "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Error: " + databaseError.getMessage());
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
                    String scannedData = scanResult.getContents();
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
                        // Update editTextHoBag with scanned data and clear after 2 seconds

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    // Update editTextSearchBag with scanned data and clear after 2 seconds
                    runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            editTextSearchBag.setText(scannedData);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editTextSearchBag.setText("");
                                }
                            }, 2000); // Delay for 2 seconds
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
                    String scannedDataRcv = scanResult.getContents();

                    // Add scanned data to detail scan results list
                    scannedResultsRcvBag.add(scannedDataRcv);
                    Log.d("Scanned Result (Detail)", scannedDataRcv);
                    int detailScanCount = scannedResultsRcvBag.size();
                    elmIncRcvBag.setText(String.valueOf(detailScanCount));
                    // Check if the scanned data exists in the main scan results (gScannedResultsHoBag)
                    int indexToRemove = -1;
                    for (int i = 0; i < gScannedResultsHoBag.size(); i++) {
                        if (gScannedResultsHoBag.get(i).equals(scannedDataRcv)) {
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
                    runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            editTextScanRcvBag.setText(scannedDataRcv);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editTextScanRcvBag.setText("");
                                }
                            }, 2000); // Delay for 2 seconds
                        }
                    });
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


package com.example.tugasakhir;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import java.util.HashSet;
import java.util.Locale;

public class RevisiDestinasiBag extends AppCompatActivity {
    ImageButton scanButtonRevisiBag;
    ImageView backButton;
    EditText editTextRevisiBagNo,editTextTanggalRevisi,editTextUserRevisi;
    Spinner RevisiFacCode;
    EditText editTextRevisiBag;
    private int totalItems;
    private static ArrayList<String> gScannedResultsHoBag = new ArrayList<>();
    TextView elmIncBag;
    Button buttonCreateRevisiBag,approveButtonRevisiBag;
    private final String bagPrefix = "CGK_RBAG_";
    private String hoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revisi_destinasi_bag);
        editTextRevisiBag = findViewById(R.id.editTextRevisiBag);
        backButton = findViewById(R.id.backButton);
        editTextRevisiBagNo=findViewById(R.id.editTextRevisiBagNo);
        editTextTanggalRevisi=findViewById(R.id.editTextTanggalRevisi);
        editTextUserRevisi=findViewById(R.id.editTextUserRevisi);
        scanButtonRevisiBag=findViewById(R.id.scanButtonRevisiBag);
        elmIncBag = findViewById(R.id.textViewIncrementTotalBag);
        buttonCreateRevisiBag = findViewById(R.id.buttonCreateRevisiBag);
        approveButtonRevisiBag =findViewById(R.id.approveButtonRevisiBag);
        RevisiFacCode = findViewById(R.id.RevisiFacCode);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(RevisiDestinasiBag.this, MainActivity.class);
                startActivity(back);
            }
        });
        scanButtonRevisiBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScannerRevisiBag();
            }
        });
        buttonCreateRevisiBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }
            private void fillEditTextWithAutoIncrementAndTimestamp() {
                // Retrieve the saved counter value from SharedPreferences (if any)
                int savedRevisiBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("revisiBagCounter", 0);

                // Increment the counter based on the saved value
                int revisiBagCounter = savedRevisiBagCounter + 1;

                // Update the SharedPreferences with the new counter value
                getSharedPreferences("app_data", MODE_PRIVATE)
                        .edit()
                        .putInt("revisiBagCounter", revisiBagCounter)
                        .apply();


                editTextRevisiBagNo.setText(String.valueOf(bagPrefix + revisiBagCounter));

                // Mengatur tanggal dengan format timestamp
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalRevisi.setText(currentDate);

                // EditText untuk Nama User dan Origin akan diisi di method fetchUserData()
            }
        });
        DatabaseReference facilitiesRef = FirebaseDatabase.getInstance().getReference().child("facilities");

        // Create an ArrayList to hold the facility codes
        final ArrayList<String> facilityCodes = new ArrayList<>();

        facilitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final ArrayList<String> facilityList = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Assuming each child has a key representing the facility code
                        String facilityCode = childSnapshot.getKey();

                        // Access facilityName node based on the key
                        DataSnapshot facilityNameSnapshot = childSnapshot.child("facilityName");
                        if (facilityNameSnapshot.exists()) {
                            String facilityName = facilityNameSnapshot.getValue(String.class);
                            // Concatenate facility code and name
                            String facilityListItem = facilityCode + " : " + facilityName;
                            facilityList.add(facilityListItem);
                        } else {
                            // Handle case where "facilityName" node doesn't exist
                            // ...
                        }
                    }

                    // Create an ArrayAdapter with the combined facility information
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RevisiDestinasiBag.this, android.R.layout.simple_spinner_item, facilityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapter.insert("Pilih Facility Destinasi", 0);
                    // Set the adapter to the Spinner
                    RevisiFacCode.setAdapter(adapter);
                } else {
                    Toast.makeText(RevisiDestinasiBag.this, "No facilities found in Firebase.", LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RevisiDestinasiBag.this, "Failed to retrieve facility codes: " + databaseError.getMessage(), LENGTH_SHORT).show();
            }
        });
        approveButtonRevisiBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validation (Optional)
                if (editTextRevisiBagNo.getText().toString().isEmpty() ||
                        editTextUserRevisi.getText().toString().isEmpty() ||
                        RevisiFacCode.getSelectedItem().toString().trim().isEmpty() ||
                        editTextTanggalRevisi.getText().toString().isEmpty() ||
                        gScannedResultsHoBag.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields and scan items", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prepare BagData object
                BagDataRevisiBag newBag = new BagDataRevisiBag(

                        editTextRevisiBagNo.getText().toString().trim(),
                        editTextUserRevisi.getText().toString().trim(),
                        editTextTanggalRevisi.getText().toString().trim(),
                        RevisiFacCode.getSelectedItem().toString().trim(),  // Assuming facility code is selected from spinner
                        gScannedResultsHoBag
                );

                // Firebase Database interaction
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference();

                // Create a reference for the specific bag under RevisiBags (modify node name as needed)
                DatabaseReference bagRef = bagsRef.child("RevisiBags").child(editTextRevisiBagNo.getText().toString().trim());

                // Save data to RevisiBags node directly
                bagRef.setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data saved successfully in RevisiBags
                                Toast.makeText(getApplicationContext(), "Data berhasil diapprove", Toast.LENGTH_SHORT).show();
                                updateFacilityCodeInHoBags(hoId, RevisiFacCode.getSelectedItem().toString().trim());
                                gScannedResultsHoBag.clear();
                                editTextRevisiBagNo.setText("");
                                editTextTanggalRevisi.setText("");
                                editTextUserRevisi.setText("");
                                RevisiFacCode.setSelection(0);
                                elmIncBag.setText("0");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle errors saving to RevisiBags
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data di RevisiBags: " + e.getMessage(), LENGTH_SHORT).show();
                            }
                        });

                // Additional actions after approval (Optional)
                // - Update status of scanned items in bagsToHo (if applicable)
                // - Clear UI elements


            }
        });
    }

    private void updateFacilityCodeInHoBags(String hoId, String newFacilityCode) {
        DatabaseReference hoBagsRef = FirebaseDatabase.getInstance().getReference("HoBags");

        // Create a query to find all bags with the matching HoId
        Query query = hoBagsRef.orderByChild("HoId").equalTo(hoId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String bagIndex = childSnapshot.getKey();
                        hoBagsRef.child(bagIndex).child("facilityCode").setValue(newFacilityCode);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle query cancellation errors
            }
        });
    }

    private void fetchUserData() {
        TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
        String username = app.getUsername();

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming you want to fetch the data of the first user found
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming 'username' and 'origin' are child nodes under each userz
                        editTextUserRevisi.setText(username != null ? username : "User");

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

    private void startBarcodeScannerRevisiBag() {
        // Inisialisasi IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(RevisiDestinasiBag.this);
        integrator.setRequestCode(50006);
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
        if (requestCode == 50006) {
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
                            editTextRevisiBag.setText(scannedData);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editTextRevisiBag.setText("");
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
        }
    }
}
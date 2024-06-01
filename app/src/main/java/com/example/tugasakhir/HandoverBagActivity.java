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
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HandoverBagActivity extends AppCompatActivity {
    ImageButton scanButtonHoBag;
    Button buttonViewDetailHoBag, buttonCreateHoBag,approveButtonHoBag;
    ImageView backButton;
    ListView listViewScannedResultsHoBag;
    ArrayList<String> gScannedResultsHoBag;
    BagDataStore bagCtx;
    TextView elmIncBag;
    EditText editTextHoBagNo, editTextTanggalHo, editTextUserHo,editTextHoBag;
    Spinner facilityCodeSpinner;
    private int hoBagCounter = 0;
    private final String bagPrefix = "CGK_HBAG_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handover_bag);
        editTextHoBagNo = findViewById(R.id.editTextHoBagNo);
        editTextTanggalHo = findViewById(R.id.editTextTanggalHo);
        backButton = findViewById(R.id.backButton);
        editTextUserHo = findViewById(R.id.editTextUserHo);
        scanButtonHoBag = findViewById(R.id.scanButtonHoBag);
        buttonViewDetailHoBag = findViewById(R.id.buttonViewDetailHoBag); // Initialize buttonViewDetailHoBag here
        elmIncBag = findViewById(R.id.textViewIncrementHoBag);
        gScannedResultsHoBag = new ArrayList<>();
        buttonCreateHoBag = findViewById(R.id.buttonCreateHoBag);
        facilityCodeSpinner = findViewById(R.id.editTextFacCode);
        approveButtonHoBag =findViewById(R.id.approveButtonHoBag);
        editTextHoBag=findViewById(R.id.editTextHoBag);
        // Initialize bagCtx
        bagCtx = ((TugasAkhirContext) getApplicationContext()).getBagDataStore();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(HandoverBagActivity.this, MainActivity.class);
                startActivity(back);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(HandoverBagActivity.this, android.R.layout.simple_spinner_item, facilityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapter.insert("Pilih Facility Destinasi", 0);
                    // Set the adapter to the Spinner
                    facilityCodeSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(HandoverBagActivity.this, "No facilities found in Firebase.", LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HandoverBagActivity.this, "Failed to retrieve facility codes: " + databaseError.getMessage(), LENGTH_SHORT).show();
            }
        });
        scanButtonHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScanner();
            }
        });
        buttonCreateHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }

            private void fillEditTextWithAutoIncrementAndTimestamp() {
                // Retrieve the saved counter value from SharedPreferences (if any)
                int savedHoBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("hoBagCounter", 0);

                // Increment the counter based on the saved value
                int hoBagCounter = savedHoBagCounter + 1;

                // Update the SharedPreferences with the new counter value
                getSharedPreferences("app_data", MODE_PRIVATE)
                        .edit()
                        .putInt("hoBagCounter", hoBagCounter)
                        .apply();


                editTextHoBagNo.setText(String.valueOf(bagPrefix + hoBagCounter));

                // Mengatur tanggal dengan format timestamp
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalHo.setText(currentDate);

                // EditText untuk Nama User dan Origin akan diisi di method fetchUserData()
            }
        });

        // Set onClickListener untuk tombol View Detail
        buttonViewDetailHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk memulai ButtonViewDetailActivity
                Intent intent = new Intent(HandoverBagActivity.this, ViewDetailHoBag.class);
                intent.putExtra("scannedResults", gScannedResultsHoBag);
                startActivity(intent);
            }
        });
        approveButtonHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> gScannedResultsHoBag = ((TugasAkhirContext) getApplicationContext()).getGlobalData().getScannedResults();
                String indexBag = editTextHoBagNo.getText().toString().trim();
                String user = editTextUserHo.getText().toString().trim();
                String facilityCode = facilityCodeSpinner.getSelectedItem().toString().trim(); // Assuming facility code is selected from spinner
                String tanggal = editTextTanggalHo.getText().toString().trim();

                // Validation (Optional)
                if (indexBag.isEmpty() || user.isEmpty() || facilityCode.isEmpty() || tanggal.isEmpty() || gScannedResultsHoBag.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields and scan items", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Process scanned data (Optional)
                // ...

                // Create BagData object
                BagDataHoBag newBag = new BagDataHoBag(indexBag, user, facilityCode, tanggal, gScannedResultsHoBag);

                // Firebase Database interaction
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference();

                // Create a reference for the specific bag under HoBags
                DatabaseReference bagRef = bagsRef.child("HoBags").child(indexBag);

                // Save data to HoBags node directly
                bagRef.setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data saved successfully in HoBags
                                // ...
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle errors saving to HoBags
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data di HoBags: " + e.getMessage(), LENGTH_SHORT).show();
                            }
                        });

                // Prepare data for individual scan results under bagsToHo (modify as needed)
                HashMap<String, Object> bagsToHoData = new HashMap<>();
                for (String scannedItem : gScannedResultsHoBag) {
                    HashMap<String, String> scanResultData = new HashMap<>();
                    scanResultData.put("HoId", indexBag);  // Add HoId for each scan result
                    bagsToHoData.put(scannedItem, scanResultData);  // Use scanned item as child node key
                }

                // Create a separate reference for bagsToHo at the top level
                DatabaseReference bagsToHoRef = bagsRef.child("bagsToHo");

                // Save data to bagsToHo node with individual child nodes
                bagsToHoRef.updateChildren(bagsToHoData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data saved successfully in bagsToHo
                                Toast.makeText(getApplicationContext(), "Data berhasil disimpan di Firebase", LENGTH_SHORT).show();
                                // Clear UI elements (Optional)
                                editTextHoBagNo.setText("");
                                editTextUserHo.setText("");
                                editTextTanggalHo.setText("");
                                facilityCodeSpinner.setSelection(0); // Reset facility code spinner to first item (index 0)
                                ((TugasAkhirContext) getApplicationContext()).getGlobalData().clearScannedResults(); // Clear scanned results
                                elmIncBag.setText("0"); // Set elmIncBag to empty string
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle errors saving to bagsToHo
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data di bagsToHo: " + e.getMessage(), LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> gScannedResultsHoBag = ((TugasAkhirContext) getApplicationContext()).getGlobalData().getScannedResults();
        elmIncBag.setText(String.valueOf(gScannedResultsHoBag.size()));
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
                        editTextUserHo.setText(username != null ? username : "User");

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


    private void startBarcodeScanner() {
        // Inisialisasi IntentIntegrator
        IntentIntegrator integrator = new IntentIntegrator(HandoverBagActivity.this);
        integrator.setRequestCode(50003);
        // Konfigurasi untuk IntentIntegrator
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
        integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
        integrator.initiateScan(); // Memulai pemindaian
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent bags) {
        super.onActivityResult(requestCode, resultCode, bags);
        if (requestCode == 50003) {
            IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, bags);
            if (result != null && result.getContents() != null) {
                String scannedData = result.getContents();
                ArrayList<String> gScannedResultsHoBag = ((TugasAkhirContext) getApplicationContext()).getGlobalData().getScannedResults();
                if (bagCtx != null) {
                    HashMap<String, ArrayList<String>> bagsHolder = bagCtx.getBagsHolder();
                    if (bagsHolder != null) {
                        // Create a temporary ArrayList to hold the new scan result
                        ArrayList<String> newScannedResults = new ArrayList<>();
                        newScannedResults.add(scannedData);

                        // Add new scanned results to the existing scannedResultsHoBag
                        if (gScannedResultsHoBag == null) {
                            gScannedResultsHoBag = new ArrayList<>();
                        }
                        gScannedResultsHoBag.addAll(newScannedResults);

                        // Update elmIncBag text
                        elmIncBag.setText(String.valueOf(gScannedResultsHoBag.size()));

                        // Update ListView to display the latest scan results
                        if (listViewScannedResultsHoBag != null) {
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewScannedResultsHoBag.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gScannedResultsHoBag);
                                listViewScannedResultsHoBag.setAdapter(adapter);
                            }
                        }
                        // Update editTextHoBag with scanned data and clear after 2 seconds
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editTextHoBag.setText(scannedData);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        editTextHoBag.setText("");
                                    }
                                }, 2000); // Delay for 2 seconds
                            }
                        });
                        // Optional: Print scannedResultsHoBag to check if data is added correctly
                        Log.d("ScannedResults", gScannedResultsHoBag.toString());
                    }
                }
            }
        }
    }

}

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
    EditText editTextHoBagNo, editTextTanggalHo, editTextUserHo,editTextHoBag,editTextRemarks;
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
        editTextRemarks=findViewById(R.id.editTextRemarks);
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
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(HandoverBagActivity.this, android.R.layout.simple_spinner_item, facilityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapter.insert("Pilih Facility Destinasi", 0);
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
                int savedHoBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("hoBagCounter", 0);
                int hoBagCounter = savedHoBagCounter + 1;
                getSharedPreferences("app_data", MODE_PRIVATE)
                        .edit()
                        .putInt("hoBagCounter", hoBagCounter)
                        .apply();
                editTextHoBagNo.setText(String.valueOf(bagPrefix + hoBagCounter));
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalHo.setText(currentDate);
            }
        });
        buttonViewDetailHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String remarks = editTextRemarks.getText().toString().trim();
                if (indexBag.isEmpty() || user.isEmpty() || facilityCode.isEmpty() || tanggal.isEmpty() || gScannedResultsHoBag.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all required fields and scan items", Toast.LENGTH_SHORT).show();
                    return;
                }
                BagDataHoBag newBag = new BagDataHoBag(indexBag, user, facilityCode, tanggal,remarks, gScannedResultsHoBag);
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference bagRef = bagsRef.child("HoBags").child(indexBag);
                bagRef.setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data di HoBags: " + e.getMessage(), LENGTH_SHORT).show();
                            }
                        });
                HashMap<String, Object> bagsToHoData = new HashMap<>();
                for (String scannedItem : gScannedResultsHoBag) {
                    HashMap<String, String> scanResultData = new HashMap<>();
                    scanResultData.put("HoId", indexBag);
                    bagsToHoData.put(scannedItem, scanResultData);
                }
                DatabaseReference bagsToHoRef = bagsRef.child("bagsToHo");
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
                                editTextRemarks.setText("");
                                facilityCodeSpinner.setSelection(0);
                                ((TugasAkhirContext) getApplicationContext()).getGlobalData().clearScannedResults();
                                elmIncBag.setText("0");                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
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
        TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
        String username = app.getUsername();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        editTextUserHo.setText(username != null ? username : "User");
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
        IntentIntegrator integrator = new IntentIntegrator(HandoverBagActivity.this);
        integrator.setRequestCode(50003);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
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
                        if (gScannedResultsHoBag == null) {
                            gScannedResultsHoBag = new ArrayList<>();
                        }
                        gScannedResultsHoBag.addAll(newScannedResults);
                        elmIncBag.setText(String.valueOf(gScannedResultsHoBag.size()));
                        if (listViewScannedResultsHoBag != null) {
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewScannedResultsHoBag.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gScannedResultsHoBag);
                                listViewScannedResultsHoBag.setAdapter(adapter);
                            }
                        }
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
                        Log.d("ScannedResults", gScannedResultsHoBag.toString());
                    }
                }
            }
        }
    }
}

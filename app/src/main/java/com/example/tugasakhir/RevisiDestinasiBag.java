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
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalRevisi.setText(currentDate);
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
                        String facilityCode = childSnapshot.getKey();
                        DataSnapshot facilityNameSnapshot = childSnapshot.child("facilityName");
                        if (facilityNameSnapshot.exists()) {
                            String facilityName = facilityNameSnapshot.getValue(String.class);
                            String facilityListItem = facilityCode + " : " + facilityName;
                            facilityList.add(facilityListItem);
                        } else {
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RevisiDestinasiBag.this, android.R.layout.simple_spinner_item, facilityList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapter.insert("Pilih Facility Destinasi", 0);
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
                BagDataRevisiBag newBag = new BagDataRevisiBag(
                        editTextRevisiBagNo.getText().toString().trim(),
                        editTextUserRevisi.getText().toString().trim(),
                        editTextTanggalRevisi.getText().toString().trim(),
                        RevisiFacCode.getSelectedItem().toString().trim(),  // Assuming facility code is selected from spinner
                        gScannedResultsHoBag
                );
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference bagRef = bagsRef.child("RevisiBags").child(editTextRevisiBagNo.getText().toString().trim());
                bagRef.setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
            }
        });
    }
    private void updateFacilityCodeInHoBags(String hoId, String newFacilityCode) {
        DatabaseReference hoBagsRef = FirebaseDatabase.getInstance().getReference("HoBags");
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
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        editTextUserRevisi.setText(username != null ? username : "User");
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
        if (requestCode == 50006) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(resultCode, data);
            if (scanResult != null) {
                if (scanResult.getContents() != null) {
                    String scannedData = scanResult.getContents();
                    Log.d("Scanned Result", scanResult.getContents());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bagsToHo");
                    databaseReference.child(scanResult.getContents()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String hoId = dataSnapshot.child("HoId").getValue(String.class);
                                Query query = databaseReference.orderByChild("HoId").equalTo(hoId);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                String bagIndex = childSnapshot.getKey();
                                                totalItems++;
                                                gScannedResultsHoBag.add(bagIndex);
                                            }
                                            elmIncBag.setText(String.valueOf(totalItems));
                                        } else {
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            } else {
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

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
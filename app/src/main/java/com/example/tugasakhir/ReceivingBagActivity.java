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
    private String remarks;


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
        elmIncBag = findViewById(R.id.textViewIncrementOutBag);
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
                Intent intent = new Intent(ReceivingBagActivity.this, DetailOutstandingActivity.class);
                intent.putExtra("scannedResults", gScannedResultsHoBag);
                startActivity(intent);
            }
        });
        buttonDetailScanBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                int savedRcvBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("rcvBagCounter", 0);
                int rcvBagCounter = savedRcvBagCounter + 1;
                getSharedPreferences("app_data", MODE_PRIVATE)
                        .edit()
                        .putInt("rcvBagCounter", rcvBagCounter)
                        .apply();
                editTextRcvBagNo.setText(String.valueOf(bagPrefix + rcvBagCounter));
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                editTextTanggalRcv.setText(currentDate);
            }
        });
    }

    private void handleApproveButtonClick() {
        if (editTextRcvBagNo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Receiving Bag No.", LENGTH_SHORT).show();
            return;
        }

        if (editTextTanggalRcv.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Receiving Date.", LENGTH_SHORT).show();
            return;
        }
        String bagKey = editTextRcvBagNo.getText().toString();
        BagRcvData newBagData = new BagRcvData(
                bagKey,
                editTextUserRcv.getText().toString(),
                editTextTanggalRcv.getText().toString(),
                editTextRemarksRcv.getText().toString(),
                scannedResultsRcvBag
        );
        DatabaseReference receivingBagsRef = FirebaseDatabase.getInstance().getReference().child("receivingBags");
        receivingBagsRef.child(bagKey).setValue(newBagData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Receiving Bag data saved successfully", Toast.LENGTH_SHORT).show();
                        clearUiAfterSave();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to save Receiving Bag data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void clearUiAfterSave() {
        editTextRcvBagNo.setText("");
        editTextTanggalRcv.setText("");
        editTextUserRcv.setText("");
        editTextRemarksRcv.setText("");
        editTextSearchBag.setText("");
        editTextScanRcvBag.setText("");
        elmIncRcvBag.setText("0");
        elmIncBag.setText("0");
        ((TugasAkhirContext) getApplicationContext()).getGlobalData().clearScannedResults();
    }

    private void updateScannedResultCount() {
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
                        editTextUserRcv.setText(username != null ? username : "User");
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
        IntentIntegrator integrator = new IntentIntegrator(ReceivingBagActivity.this);
        integrator.setRequestCode(50004);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
        integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
        integrator.initiateScan(); // Memulai pemindaian
    }
    private void startBarcodeScannerRcvBag() {
        IntentIntegrator integrator = new IntentIntegrator(ReceivingBagActivity.this);
        integrator.setRequestCode(50005);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50004) {
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
                                                String remarksPath = "HoBags/" + hoId + "/remarks";
                                                DatabaseReference remarksReference = FirebaseDatabase.getInstance().getReference(remarksPath);
                                                remarksReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot remarksSnapshot) {
                                                        if (remarksSnapshot.exists()) {
                                                            remarks = remarksSnapshot.getValue(String.class);
                                                        }
                                                        editTextRemarksRcv.setText(remarks);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
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
                            editTextSearchBag.setText(scannedData);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editTextSearchBag.setText("");
                                }
                            }, 2000); // Delay for 2 seconds
                        }
                    });
                    Toast.makeText(this, "Scanned and data retrieved successfully", LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Scan canceled", LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == 50005) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(resultCode, data);
            if (scanResult != null) {
                if (scanResult.getContents() != null) {
                    String scannedDataRcv = scanResult.getContents();
                    scannedResultsRcvBag.add(scannedDataRcv);
                    Log.d("Scanned Result (Detail)", scannedDataRcv);
                    int detailScanCount = scannedResultsRcvBag.size();
                    elmIncRcvBag.setText(String.valueOf(detailScanCount));
                    int indexToRemove = -1;
                    for (int i = 0; i < gScannedResultsHoBag.size(); i++) {
                        if (gScannedResultsHoBag.get(i).equals(scannedDataRcv)) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove != -1) {
                        gScannedResultsHoBag.remove(indexToRemove);
                        totalItems--;
                        elmIncBag.setText(String.valueOf(totalItems));
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
                } else {
                    Toast.makeText(this, "Scan canceled", LENGTH_SHORT).show();
                }
            }
        }
    }
}


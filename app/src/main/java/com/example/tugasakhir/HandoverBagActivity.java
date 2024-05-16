package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    Button buttonViewDetailHoBag,buttonCreateHoBag;
    ImageView backButton;
    ListView listViewScannedResultsHoBag;
    ArrayList<String> scannedResultsHoBag;
    BagDataStore bagCtx;
    TextView elmIncBag;
    EditText editTextHoBagNo, editTextTanggalHo,editTextUserHo;
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
        scannedResultsHoBag = new ArrayList<>();
        buttonCreateHoBag= findViewById(R.id.buttonCreateHoBag);

        // Initialize bagCtx
        bagCtx = ((TugasAkhirContext) getApplicationContext()).getBagDataStore();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(HandoverBagActivity.this, MainActivity.class);
                startActivity(back);
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
        private void fillEditTextWithAutoIncrementAndTimestamp () {
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
                 intent.putExtra("scannedResults", scannedResultsHoBag);
                startActivity(intent);
            }
        });
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
                    }} else {
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

                if (bagCtx != null) {
                    HashMap<String, ArrayList<String>> bagsHolder = bagCtx.getBagsHolder();
                    if (bagsHolder != null) {
                        scannedResultsHoBag.add(scannedData); // Add scanned data to ArrayList
                        elmIncBag.setText(String.valueOf(scannedResultsHoBag.size())); // Update elmIncBag text

                        // Update ListView to display the latest scan results
                        if (listViewScannedResultsHoBag != null) {
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewScannedResultsHoBag.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        // Optional: Print scannedResultsHoBag to check if data is added correctly
                        Log.d("ScannedResults", scannedResultsHoBag.toString());
                    }

                }
            }
        }
    }
    }




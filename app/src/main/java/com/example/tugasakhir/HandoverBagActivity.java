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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.HashMap;

public class HandoverBagActivity extends AppCompatActivity {
    ImageButton scanButtonHoBag;
    Button buttonViewDetailHoBag;
    ListView listViewScannedResultsHoBag;
    ArrayList<String> scannedResultsHoBag;
    BagDataStore bagCtx;
    TextView elmIncBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handover_bag);

        scanButtonHoBag = findViewById(R.id.scanButtonHoBag);
        buttonViewDetailHoBag = findViewById(R.id.buttonViewDetailHoBag); // Initialize buttonViewDetailHoBag here
        elmIncBag = findViewById(R.id.textViewIncrementHoBag);
        scannedResultsHoBag = new ArrayList<>();


        // Initialize bagCtx
        bagCtx = ((TugasAkhirContext) getApplicationContext()).getBagDataStore();

        scanButtonHoBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScanner();
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




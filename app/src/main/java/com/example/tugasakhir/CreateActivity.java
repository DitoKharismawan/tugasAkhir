package com.example.tugasakhir;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.activity.OnBackPressedCallback;

public class CreateActivity extends AppCompatActivity {
    ImageView backButton;
    EditText editTextAwbBag, editTextTanggal, editTextUser, editTextOrigin, editTextFacCode,editTextRemarks;
    TextView elmIncConnote, elmIncBag;
    EditText elmEditTextBag, elmEditTextConnote;
    Button buttonCreate, buttonViewDetail,approveButton;
    private int bagCounter = 0;
    private final String bagPrefix = "CGK_HACB_";
    ImageButton scanButtonBag, scanButtonConnote;
    TugasAkhirContext taCtx;
    BagDataStore bagCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        editTextAwbBag = findViewById(R.id.editTextAwbBag);
        editTextTanggal = findViewById(R.id.editTextTanggal);
        editTextUser = findViewById(R.id.editTextUser);
        editTextOrigin = findViewById(R.id.editTextOrigin);
        editTextFacCode = findViewById(R.id.editTextFacCode);
        editTextRemarks = findViewById(R.id.editTextRemarks);
        buttonCreate = findViewById(R.id.buttonCreate);
        backButton = findViewById(R.id.backButton);
        scanButtonBag = findViewById(R.id.scanButtonBag);
        scanButtonConnote = findViewById(R.id.scanButtonConnote);
        buttonViewDetail = findViewById(R.id.buttonViewDetail);
        approveButton = findViewById(R.id.approveButton);
        elmIncBag = findViewById(R.id.textViewIncrementBag);
        elmIncConnote = findViewById(R.id.textIncrementConnote);
        elmIncBag = findViewById(R.id.textViewIncrementBag);
        elmEditTextBag = findViewById(R.id.editTextBag);
        elmEditTextConnote = findViewById(R.id.editTextConnote);
        taCtx = (TugasAkhirContext)getApplicationContext();
        bagCtx = taCtx.getBagDataStore();
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
        buttonViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateActivity.this, ButtonViewDetailActivity.class);
                intent.putExtra("bagsHolder", bagCtx.getBagsHolder());
                startActivity(intent);
            }
        });
        scanButtonBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
                integrator.setRequestCode(50001);
                integrator.setPrompt("Scan a barcode");
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });
        scanButtonConnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elmEditTextBag.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Silakan scan nomor BAG terlebih dahulu", Toast.LENGTH_LONG).show();
                }
                else {
                    IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
                    integrator.setRequestCode(50002);
                    integrator.setPrompt("Scan a barcode");
                    integrator.setBeepEnabled(true);
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                }
            }
        });
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String awbKey = elmEditTextBag.getText().toString();
                String indexBag = editTextAwbBag.getText().toString();
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference().child("bags");
                BagData newBag = new BagData(
                        indexBag,
                        editTextUser.getText().toString(),
                        editTextRemarks.getText().toString(),
                        editTextOrigin.getText().toString(),
                        editTextFacCode.getText().toString(),
                        editTextTanggal.getText().toString(),
                        bagCtx.getBagsHolder()
                );
                int totalConnote = newBag.calculateTotalConnote();
                newBag.setTotalConnote(totalConnote);
                bagsRef.child(awbKey).setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Data berhasil disimpan di Firebase", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                editTextAwbBag.setText("");
                editTextUser.setText("");
                editTextOrigin.setText("");
                editTextFacCode.setText("");
                editTextTanggal.setText("");
                elmEditTextBag.setText("");
                editTextRemarks.setText("");
                elmEditTextConnote.setText("");
                elmIncBag.setText("0");
                elmIncConnote.setText("0");
                bagCtx.getBagsHolder().clear();

            }
        });
    }
    @Override
    protected void onResume() {
      super.onResume();
      updateCountDisplay();
    }
    private void fillEditTextWithAutoIncrementAndTimestamp() {
        int savedBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("bagCounter", 0);
        bagCounter = savedBagCounter + 1;
        getSharedPreferences("app_data", MODE_PRIVATE)
                .edit()
                .putInt("bagCounter", bagCounter)
                .apply();
        editTextAwbBag.setText(String.valueOf(bagPrefix + bagCounter));
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        editTextTanggal.setText(currentDate);
    }
    private void fetchUserData() {
        TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
        String username = app.getUsername();
        String origin = app.getOrigin();
        String fCode = app.getFCode();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        editTextUser.setText(username != null ? username : "User");
                        editTextOrigin.setText(origin != null ? origin : "Origin");
                        editTextFacCode.setText(fCode != null ? fCode : "Facility Code");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent awb) {
        super.onActivityResult(requestCode, resultCode, awb);
        if (requestCode == 50001) {
            IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, awb);
            if (result != null) {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();
                    if (bagCtx.getBagsHolder().containsKey(scannedData)) {
                        Toast.makeText(getApplicationContext(), "BAG " + scannedData + " sudah ada sebelumnya!", Toast.LENGTH_LONG).show();
                        elmEditTextBag.setText(scannedData);
                        elmIncConnote.setText(bagCtx.getTotalConnoteCount() + "");
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "[SCAN BAG] = " + scannedData, Toast.LENGTH_LONG).show();

                        bagCtx.appendBag(scannedData);
                        elmIncBag.setText(bagCtx.getBagsHolder().size() + "");
                        elmEditTextBag.setText(scannedData);

                    }
                }
            }
        }
        else if (requestCode == 50002) {
            // CONNOTE
            IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, awb);
            if (result != null) {
                if (result.getContents() != null) {
                    String scannedData = result.getContents(); // Data barcode yang discan
                    // Tambahkan hasil scan ke dalam ArrayList
                    String currentBag = elmEditTextBag.getText().toString();
                    if (!bagCtx.getBagsHolder().containsKey(currentBag)) {
                        bagCtx.appendBag(currentBag);
                        Toast.makeText(getApplicationContext(), "Bag Baru : " + currentBag + " Connote : " + scannedData, Toast.LENGTH_LONG).show();
                    }
                    else if (bagCtx.getConnoteBag(scannedData) != null) {
                        showConfirmDialog(
                                "Connote sudah ada",
                                "Yakin pindahkan connote " + scannedData + " ke BAG " + currentBag + "?")
                                .setPositiveButton(
                                "Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (bagCtx.moveConnote(scannedData, currentBag)) {
                                            Toast.makeText(getApplicationContext(), "Berhasil memindahkan connote", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Gagal memindahkan", Toast.LENGTH_LONG).show();
                                        };
                                    }
                                })
                                .setNegativeButton(
                                        "Batal",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Do Something Here
                                                Toast.makeText(getApplicationContext(), "Aksi dibatalkan", Toast.LENGTH_LONG).show();
                                            }
                                }).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Connote " + scannedData + " untuk BAG " + currentBag + " ditambahkan!", Toast.LENGTH_LONG).show();
                        bagCtx.appendConnote(currentBag, scannedData);
                    }
                    elmEditTextConnote.setText(scannedData);
                    updateCountDisplay();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            elmEditTextConnote.setText("");
                        }
                    }, 2000);
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Unhandled ActivityResult : " + requestCode, Toast.LENGTH_LONG).show();
        }
    }
    private void updateCountDisplay() {
        elmIncBag.setText(bagCtx.getBagsHolder().size() + "");
        elmIncConnote.setText(bagCtx.getTotalConnoteCount() + "");
    }
    private AlertDialog.Builder showConfirmDialog(String judul, String pesan) {
        return new AlertDialog.Builder(this)
                .setTitle(judul)
                .setMessage(pesan)
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert));
    }
}

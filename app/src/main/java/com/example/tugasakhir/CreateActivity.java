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
//    ArrayList<String> scannedResults; // ArrayList untuk menyimpan hasil scan barcode
//    ArrayList<String> scansBag;
//    ArrayList<String> scansConnote;
//    HashMap<String, ArrayList<String>> bagsHolder;

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

//        scannedResults = new ArrayList<>(); // Inisialisasi ArrayList
//        scansBag = new ArrayList<>(); // Inisialisasi ArrayList
//        scansConnote = new ArrayList<>();
//        bagsHolder = new HashMap<>();
        taCtx = (TugasAkhirContext)getApplicationContext();
        bagCtx = taCtx.getBagDataStore();

        // Set onClickListener untuk tombol Create
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }
        });

        // Set onClickListener untuk tombol Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(back);
            }
        });

        // Set onClickListener untuk tombol View Detail
        buttonViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk memulai ButtonViewDetailActivity
//                Toast.makeText(getApplicationContext(), "[CreateActivity] OnClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateActivity.this, ButtonViewDetailActivity.class);
                // Kirimkan data pemindaian ke ButtonViewDetailActivity
//                intent.putStringArrayListExtra("scannedResults", scannedResults);
//                intent.putExtra("scansBag", scansBag);
                intent.putExtra("bagsHolder", bagCtx.getBagsHolder());
                startActivity(intent);
            }
        });


        // Set onClickListener untuk tombol Scan Bag
        scanButtonBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inisialisasi IntentIntegrator
                IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
                integrator.setRequestCode(50001);
                // Konfigurasi untuk IntentIntegrator
                integrator.setPrompt("Scan a barcode");
                integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
                integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
                integrator.initiateScan(); // Memulai pemindaian
            }
        });

        // Set onClickListener untuk tombol Scan Connote
        scanButtonConnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elmEditTextBag.getText().length() == 0) {
//                    Toast.makeText(getApplicationContext(), "[C] " + elmEditTextBag.getText() + " [S] " + elmEditTextBag.getText().length(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Silakan scan nomor BAG terlebih dahulu", Toast.LENGTH_LONG).show();
                }
                else {
                    // Inisialisasi IntentIntegrator
                    IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
                    // Konfigurasi untuk IntentIntegrator
                    integrator.setRequestCode(50002);
                    integrator.setPrompt("Scan a barcode");
                    integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
                    integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
                    integrator.initiateScan(); // Memulai pemindaian
                }
            }
        });
        // Set onClickListener untuk tombol Create
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String awbKey = elmEditTextBag.getText().toString();
                String indexBag = editTextAwbBag.getText().toString();
                // Mendapatkan referensi database
                DatabaseReference bagsRef = FirebaseDatabase.getInstance().getReference().child("bags");

                // Membuat objek untuk data tas baru
                BagData newBag = new BagData(
                        indexBag,
                        editTextUser.getText().toString(),
                        editTextRemarks.getText().toString(),
                        editTextOrigin.getText().toString(),
                        editTextFacCode.getText().toString(),
                        editTextTanggal.getText().toString(),
                        bagCtx.getBagsHolder()
                );

                // Menghitung total connote
                int totalConnote = newBag.calculateTotalConnote();

                // Menambahkan totalConnote ke data tas baru
                newBag.setTotalConnote(totalConnote);

                // Menyimpan data tas baru ke Firebase

                // Menyimpan data tas ke Firebase
                bagsRef.child(awbKey).setValue(newBag)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data berhasil disimpan
                                Toast.makeText(getApplicationContext(), "Data berhasil disimpan di Firebase", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Penanganan jika ada kesalahan saat menyimpan data
                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                // Mengosongkan nilai-nilai pada EditText
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
                // Mengosongkan ArrayList scannedResults, scansBag, scansConnote, dan bagsHolder
//                scannedResults.clear();
//                scansBag.clear();
//                scansConnote.clear();
                bagCtx.getBagsHolder().clear();

            }
        });

//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                // Whatever you want
//                // when back pressed
//                Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onResume() {
      super.onResume();
      updateCountDisplay();
    }

    private void fillEditTextWithAutoIncrementAndTimestamp() {
        // Retrieve the saved counter value from SharedPreferences (if any)
        int savedBagCounter = getSharedPreferences("app_data", MODE_PRIVATE).getInt("bagCounter", 0);

        // Increment the counter based on the saved value
        bagCounter = savedBagCounter + 1;

        // Update the SharedPreferences with the new counter value
        getSharedPreferences("app_data", MODE_PRIVATE)
                .edit()
                .putInt("bagCounter", bagCounter)
                .apply();


        editTextAwbBag.setText(String.valueOf(bagPrefix + bagCounter));

        // Mengatur tanggal dengan format timestamp
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        editTextTanggal.setText(currentDate);

        // EditText untuk Nama User dan Origin akan diisi di method fetchUserData()
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
                    // Assuming you want to fetch the data of the first user found
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming 'username' and 'origin' are child nodes under each user
                        editTextUser.setText(username != null ? username : "User");
                        editTextOrigin.setText(origin != null ? origin : "Origin");
                        editTextFacCode.setText(fCode != null ? fCode : "Facility Code");
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

    // Override onActivityResult to handle the scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent awb) {
        super.onActivityResult(requestCode, resultCode, awb);
        if (requestCode == 50001) {
            IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, awb);
            if (result != null) {
                if (result.getContents() != null) {
                    String scannedData = result.getContents(); // Data barcode yang discan
                    // Tambahkan hasil scan ke dalam ArrayList
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
//                        scannedResults.add(scannedData);
//                    Toast.makeText(getApplicationContext(), "[SCN DATAS] = " + scannedResults.toString(), Toast.LENGTH_LONG).show();
//                        scansConnote.add(scannedData);
//                        bagsHolder.get(currentBag).add(scannedData);
                        bagCtx.appendConnote(currentBag, scannedData);
                    }
//                    elmIncBag.setText(scansBag.size() + "");
                    elmEditTextConnote.setText(scannedData);
//                    elmIncConnote.setText(scansConnote.size() + "");
                    updateCountDisplay();
//                    TextView elementText = findViewById(R.id.textIncrementConnote);
//                    elementText.setText(scansConnote.size());
                    // Menggunakan Handler untuk menghapus teks setelah 1 detik
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Mengosongkan teks pada elmEditTextBag
                            // ini knp dikosongin to
                            elmEditTextConnote.setText("");
                        }
                    }, 2000); // Delay 2 detik (2000 milidetik)
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
                .setMessage(
                        pesan)
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert));
    }
}

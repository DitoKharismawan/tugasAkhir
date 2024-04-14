package com.example.tugasakhir;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tugasakhir.ButtonViewDetailActivity;
import com.example.tugasakhir.MainActivity;
import com.example.tugasakhir.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CreateActivity extends AppCompatActivity {
    ImageView backButton;
    EditText editTextAwbBag, editTextTanggal, editTextUser, editTextOrigin, editTextFacCode;
    Button buttonCreate, buttonViewDetail;
    private int bagCounter = 0;
    private final String bagPrefix = "CGK/HACB/";
    ImageButton scanButtonBag, scanButtonConnote;
    ArrayList<String> scannedResults; // ArrayList untuk menyimpan hasil scan barcode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        editTextAwbBag = findViewById(R.id.editTextAwbBag);
        editTextTanggal = findViewById(R.id.editTextTanggal);
        editTextUser = findViewById(R.id.editTextUser);
        editTextOrigin = findViewById(R.id.editTextOrigin);
        editTextFacCode = findViewById(R.id.editTextFacCode);
        buttonCreate = findViewById(R.id.buttonCreate);
        backButton = findViewById(R.id.backButton);
        scanButtonBag = findViewById(R.id.scanButtonBag);
        scanButtonConnote = findViewById(R.id.scanButtonConnote);
        buttonViewDetail = findViewById(R.id.buttonViewDetail);

        scannedResults = new ArrayList<>(); // Inisialisasi ArrayList

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
                Intent intent = new Intent(CreateActivity.this, ButtonViewDetailActivity.class);
                // Kirimkan data pemindaian ke ButtonViewDetailActivity
                intent.putStringArrayListExtra("scannedResults", scannedResults);
                startActivity(intent);
            }
        });


        // Set onClickListener untuk tombol Scan Bag
        scanButtonBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inisialisasi IntentIntegrator
                IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
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
                // Inisialisasi IntentIntegrator
                IntentIntegrator integrator = new IntentIntegrator(CreateActivity.this);
                // Konfigurasi untuk IntentIntegrator
                integrator.setPrompt("Scan a barcode");
                integrator.setBeepEnabled(true); // Set true jika ingin memainkan suara saat pemindaian berhasil
                integrator.setOrientationLocked(false); // Set true jika ingin mengunci orientasi layar saat pemindaian
                integrator.initiateScan(); // Memulai pemindaian
            }
        });
    }

    private void fillEditTextWithAutoIncrementAndTimestamp() {
        // Increment untuk Bag
        bagCounter++; // Increment nilai
        editTextAwbBag.setText(String.valueOf(bagPrefix + bagCounter));

        // Mengatur tanggal dengan format timestamp
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        editTextTanggal.setText(currentDate);

        // EditText untuk Nama User dan Origin akan diisi di method fetchUserData()
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
                        String origin = userSnapshot.child("origin").getValue(String.class);
                        String fCode = userSnapshot.child("fCode").getValue(String.class);
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
        // Tangani hasil pemindaian
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, awb);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents(); // Data barcode yang discan
                // Tambahkan hasil scan ke dalam ArrayList
                scannedResults.add(scannedData);
            }
        }
    }
}

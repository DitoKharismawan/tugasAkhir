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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateActivity extends AppCompatActivity {
    ImageView backButton;
    EditText editTextAwbBag, editTextTanggal, editTextUser, editTextOrigin,editTextFacCode;
    Button buttonCreate;
    private int bagCounter = 0;
    private final String bagPrefix = "CGK/HACB/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        editTextAwbBag = findViewById(R.id.editTextAwbBag);
        editTextTanggal = findViewById(R.id.editTextTanggal);
        editTextUser = findViewById(R.id.editTextUser);
        editTextOrigin = findViewById(R.id.editTextOrigin);
        editTextFacCode= findViewById(R.id.editTextFacCode);
        buttonCreate = findViewById(R.id.buttonCreate);
        backButton = findViewById(R.id.backButton);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillEditTextWithAutoIncrementAndTimestamp();
                fetchUserData();
            }
        });

    }

    private void fillEditTextWithAutoIncrementAndTimestamp() {
        // Increment untuk Bag
        bagCounter++; // Increment nilai
        editTextAwbBag.setText(String.valueOf(bagPrefix+bagCounter));

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(back);
            }

        });
    }
}



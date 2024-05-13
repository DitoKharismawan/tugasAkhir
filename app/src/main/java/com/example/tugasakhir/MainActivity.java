package com.example.tugasakhir;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    CardView createBag, handOverBag;
    TextView textViewUser, textViewOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewUser = findViewById(R.id.textViewUser);
        textViewOrigin = findViewById(R.id.textViewOrigin);

        createBag = findViewById(R.id.createBag);
        handOverBag = findViewById(R.id.handOverBag);

        fetchUserDataMain();

        createBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(create);
            }
        });
        handOverBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(MainActivity.this, HandoverBagActivity.class);
                startActivity(create);
            }
        });

        // Logout functionality
        findViewById(R.id.logOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void fetchUserDataMain() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming you want to fetch the data of the first user found
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming 'username' and 'origin' are child nodes under each user
                        String username = userSnapshot.child("username").getValue(String.class);
                        String origin = userSnapshot.child("origin").getValue(String.class);
                        textViewUser.setText(username != null ?"Halo, "+ username : "User");
                        textViewOrigin.setText(origin!=null?"Lokasi, "+origin :"User");
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

    private void logoutUser() {
        // Clear any session data or preferences related to the user
        // For example, you can clear SharedPreferences here
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        preferences.edit().clear().apply();
        // Start LoginActivity and clear the task stack
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish MainActivity to prevent going back
    }
}

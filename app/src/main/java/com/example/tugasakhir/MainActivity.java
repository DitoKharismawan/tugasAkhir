package com.example.tugasakhir;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    CardView createBag, handOverBag, receivingBag,revisiDestBag,report;
    TextView textViewUser, textViewOrigin;
    Dialog dialog;
    Button btnDialogCancel,btnDialogLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewUser = findViewById(R.id.textViewUser);
        textViewOrigin = findViewById(R.id.textViewOrigin);
        dialog= new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);
        btnDialogCancel=dialog.findViewById(R.id.btnDialogCancel);
        btnDialogLogout=dialog.findViewById(R.id.btnDialogLogout);
        createBag = findViewById(R.id.createBag);
        handOverBag = findViewById(R.id.handOverBag);
        receivingBag = findViewById(R.id.receivingBag);
        revisiDestBag = findViewById(R.id.revisiDestBag);
        report=findViewById(R.id.report);
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
        receivingBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(MainActivity.this, ReceivingBagActivity.class);
                startActivity(create);
            }
        });
        revisiDestBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(MainActivity.this, RevisiDestinasiBag.class);
                startActivity(create);
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(create);
            }
        });
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDialogLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
                dialog.dismiss();
            }
        });
        // Logout functionality
        findViewById(R.id.logOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialog.show();
            }
        });
    }
    private void fetchUserDataMain() {
        TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
        String username = app.getUsername();
        String origin = app.getOrigin();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        textViewUser.setText(username != null ?"Halo, "+ username : "User");
                        textViewOrigin.setText(origin!=null?origin :"User");
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
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        preferences.edit().clear().apply();
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

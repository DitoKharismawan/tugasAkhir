package com.example.tugasakhir;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tugasakhir.models.PanggilBalik;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GlobalRDBStore {
    private GlobalConstant gConstant = new GlobalConstant();
    private Task<Void> setAwbCount(Integer value, PanggilBalik dataListener) {
        return fireGetCfgRef().push().setValue(value);
    }

    private void getAwbCount(Integer value, PanggilBalik dataListener) {
        fireGetCfgRef().child("awbCount").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Data dapat berupa single value atau Map jika memiliki child
                        Object o = dataSnapshot.getValue();
                        if (o instanceof Integer) {
                            dataListener.kepanggil(o);
                        }
                        else {

                        }

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log error atau tampilkan pesan error
                    Log.e("DetailConnoteActivity", "Error loading data", databaseError.toException());
                }
            }

        );
    }


    private DatabaseReference fireGetCfgRef() {
        return FirebaseDatabase.getInstance().getReference().child(gConstant.FIREBASE_CONFIG_KEY);
    }
}

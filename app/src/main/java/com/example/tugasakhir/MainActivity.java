package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    CardView createBag, handOverBag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBag = findViewById(R.id.createBag);
        handOverBag = findViewById(R.id.handOverBag);
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
    }
}
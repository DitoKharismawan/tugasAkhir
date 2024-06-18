package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportActivity extends AppCompatActivity {
Button buttonReportHACB,buttonReportHBAG,buttonReportRCVB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    buttonReportHACB = findViewById(R.id.buttonReportHACB);
    buttonReportHBAG = findViewById(R.id.buttonReportHBAG);
    buttonReportRCVB = findViewById(R.id.buttonReportRCVB);
buttonReportHACB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(ReportActivity.this, ReportHACB.class);
                startActivity(create);
            }
        });
        buttonReportHBAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(ReportActivity.this, ReportHBAG.class);
                startActivity(create);
            }
        });
        buttonReportRCVB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(ReportActivity.this, ReportRCVB.class);
                startActivity(create);
            }
        });
    }
}
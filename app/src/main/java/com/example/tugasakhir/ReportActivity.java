package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {
Button buttonReportHACB,buttonReportHBAG,buttonReportRCVB;
ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        backButton = findViewById(R.id.backButton);
        buttonReportHACB = findViewById(R.id.buttonReportHACB);
        buttonReportHBAG = findViewById(R.id.buttonReportHBAG);
        buttonReportRCVB = findViewById(R.id.buttonReportRCVB);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ReportActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
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
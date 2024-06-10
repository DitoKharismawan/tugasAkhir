package com.example.tugasakhir;

import android.app.DatePickerDialog;  // Import for DatePickerDialog
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText; // Assuming you have an EditText for displaying the date
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportHACB extends AppCompatActivity {

    private ImageButton imageButtonCalendar;
    private EditText editTextDate;  // Declare EditText for date
    ImageView printPdf;
    private ViewGroup tableLayout;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_report_hacb); // Set the layout file

        imageButtonCalendar = findViewById(R.id.imageButtonCalendar);
        editTextDate = findViewById(R.id.editTextDate);  // Find the EditText
        printPdf=findViewById(R.id.printPdf);
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        printPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public void showDatePicker() {
        // Use DatePickerDialog instead of DatePickerFragment
        DatePickerDialog datePicker = new DatePickerDialog(this);
        datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update the EditText with the selected date (format as needed)
                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year);
                editTextDate.setText(selectedDate);
            }
        });
        datePicker.show();
    }


}
package com.example.tugasakhir;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportRCVB extends AppCompatActivity {

    private ImageButton imageButtonCalendar;
    private EditText editTextDateRCVB;
    private ImageView printPdfRCVB,backButton;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_rcvb);

        imageButtonCalendar = findViewById(R.id.imageButtonCalendar);
        editTextDateRCVB = findViewById(R.id.editTextDateRCVB);
        printPdfRCVB = findViewById(R.id.printPdfRCVB);
        backButton = findViewById(R.id.backButton);
        // Initialize Firebase database
        database = FirebaseDatabase.getInstance();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ReportRCVB.this, ReportActivity.class);
                startActivity(back);
            }
        });
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        printPdfRCVB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPrintPdf();
            }
        });
    }

    private void createAndPrintPdf() {
        String selectedDate = editTextDateRCVB.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date parsedDate = null;

        try {
            parsedDate = dateFormat.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestampPrefix = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(parsedDate);

        Query query = database.getReference("receivingBags");
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // Filter dataSnapshot to only include entries with matching date
                        List<DataSnapshot> matchingSnapshots = new ArrayList<>();
                        for (DataSnapshot bagSnapshot : dataSnapshot.getChildren()) {
                            String timestamp = bagSnapshot.child("timestamp").getValue(String.class);
                            if (timestamp != null && timestamp.startsWith(timestampPrefix)) {
                                matchingSnapshots.add(bagSnapshot);
                            }
                        }

                        if (!matchingSnapshots.isEmpty()) {
                            generatePdf();
                        } else {
                            Toast.makeText(ReportRCVB.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReportRCVB.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportRCVB.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generatePdf() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("receivingBags");
        String selectedTimestamp = editTextDateRCVB.getText().toString();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Document document = new Document();
                FileOutputStream outputStream = null;
        try {
            // Create output stream and initialize PdfWriter
            File pdfFile = new File(getExternalFilesDir(null), "report_rcvb.pdf");
            outputStream = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, outputStream);

            // Open the document
            document.open();

            // Add report title
            Paragraph reportTitle = new Paragraph("RCVB Report - " + editTextDateRCVB.getText().toString());
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setFont(new Font(Font.FontFamily.HELVETICA, 50, Font.BOLD));
            document.add(reportTitle);
            document.add(new Paragraph(" "));

            // Add a table to display data
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // Set table header cells
            String[] headers = {"id RCVB", "Bag", "Remarks", "User", "Timestamp"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header));
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(headerCell);
            }

            // Add data to table rows
            String selectedDate = editTextDateRCVB.getText().toString();
            // Add data to table rows
            for (DataSnapshot bagSnapshot : dataSnapshot.getChildren()){
                String bagId = bagSnapshot.child("bagRcvId").getValue(String.class);
                String HoBag = bagSnapshot.child("scannedResultsRcvBag").getValue(Boolean.parseBoolean(toString().replace("", "").replace("", ""))).toString();
                String remarks = bagSnapshot.child("remarks").getValue(String.class);
                String user = bagSnapshot.child("user").getValue(String.class);
                String timestamp = bagSnapshot.child("timestamp").getValue(String.class);
                if (timestamp != null && timestamp.startsWith(selectedDate)) {
                    table.addCell(new PdfPCell(new Phrase(bagId)));
                    table.addCell(new PdfPCell(new Phrase(HoBag)));
                    table.addCell(new PdfPCell(new Phrase(remarks)));
                    table.addCell(new PdfPCell(new Phrase(user)));
                    table.addCell(new PdfPCell(new Phrase(timestamp)));
                } else {
                    Log.i("ReportHBAG", "No 'bagCtx' data found in snapshot!");
                }
            }

            // Add table to the document
            document.add(table);

            // Close the document
            document.close();
            outputStream.close();

            // Show success message
            Toast.makeText(ReportRCVB.this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ReportRCVB.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        openPdf();
    }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReportRCVB", "Error retrieving data", databaseError.toException());
                Toast.makeText(ReportRCVB.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openPdf() {
        File file = new File(getExternalFilesDir(null), "report_rcvb.pdf");
        if (file.exists()) {
            Uri pdfUri = FileProvider.getUriForFile(this, "com.example.tugasakhir.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Optional: start activity in new task
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to the URI
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("ReportRCVB", "PDF not found!");
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(this);
        datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year);
                editTextDateRCVB.setText(selectedDate);
            }
        });
        datePicker.show();
    }
}

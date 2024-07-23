package com.example.tugasakhir;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText; // Assuming you have an EditText for displaying the date
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
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportHACB extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ImageButton imageButtonCalendar;
    private EditText editTextDate;  // Declare EditText for date
    ImageView printPdf,backButton;;
    private ViewGroup tableLayout;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_hacb); // Set the layout file
        imageButtonCalendar = findViewById(R.id.imageButtonCalendar);
        editTextDate = findViewById(R.id.editTextDate);  // Find the EditText
        printPdf = findViewById(R.id.printPdf);
        backButton = findViewById(R.id.backButton);
        database = FirebaseDatabase.getInstance();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ReportHACB.this, ReportActivity.class);
                startActivity(back);
            }
        });
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        printPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPrintPdf();
            }
        });
    }
    private void createAndPrintPdf() {
        String selectedDate = editTextDate.getText().toString();
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

        Query query = database.getReference("bags");
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
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
                            Toast.makeText(ReportHACB.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReportHACB.this, "No data found for this date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportHACB.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void generatePdf() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bags");
        String selectedTimestamp = editTextDate.getText().toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd"); // Assuming input format is "yyyy-MM-dd"
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID")); // Full weekday name, then year-month-day
        // Parse the date using inputFormat
        Date date;
        try {
            date = inputFormat.parse(selectedTimestamp);
        } catch (ParseException e) {
            Log.e("ReportHACB", "Error parsing date: " + e.getMessage());
            date = new Date();
        }
        String formattedDate = outputFormat.format(date);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Document document = new Document();
                FileOutputStream outputStream = null;
                try {
                    File pdfFile = new File(getExternalFilesDir(null), "report.pdf");
                    outputStream = new FileOutputStream(pdfFile);
                    PdfWriter.getInstance(document, outputStream);
                    // Open the document
                    document.open();
                    // Get resources object
                    Resources resources = getResources();
                    // Get drawable resource identifier
                    int drawableId = resources.getIdentifier("jne", "drawable", getPackageName());
                    // Create drawable object
                    Drawable drawable = resources.getDrawable(drawableId);
                    // Convert drawable to bitmap (assuming drawable is a bitmap)
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    // Rest of the code using the bitmap
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image companyLogo = Image.getInstance(stream.toByteArray());
                    companyLogo.setAbsolutePosition(25, 700);
                    companyLogo.scalePercent(10);
                    document.add(companyLogo);
                    document.add(new Paragraph(" "));
                    // Membuat font tebal dengan ukuran tertentu
                    Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                    Font sizeFont = FontFactory.getFont(FontFactory.HELVETICA, 15);
                    Paragraph reportTitle = new Paragraph("Handover Create AWB to BAG Report",boldFont);
                    reportTitle.setAlignment(Element.ALIGN_RIGHT);
                    reportTitle.setFont(new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD));
                    document.add(reportTitle);
                    document.add(new Paragraph(" "));
                    // Add a table to display bagCtx data
                    PdfPTable table = new PdfPTable(7);
                    table.setWidthPercentage(105);
                    // Set table header cells
                    String[] headers = {"Bag ID","Bag = Connote", "Remarks", "Origin", "Total Connote", "User", "Tanggal"};
                    for (String header : headers) {
                        PdfPCell headerCell = new PdfPCell(new Phrase(header,sizeFont));
                        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(headerCell);
                    }
                    String selectedDate = editTextDate.getText().toString();
                    // Add bagCtx data to table rows
                    for (DataSnapshot bagSnapshot : dataSnapshot.getChildren()) {
                        String bagId = bagSnapshot.child("bagId").getValue(String.class);
                        String bagCtx = bagSnapshot.child("bagCtx").getValue(Boolean.parseBoolean(toString().replace("", "").replace("", ""))).toString();
                        String remarks = bagSnapshot.child("remarks").getValue(String.class);
                        String origin = bagSnapshot.child("origin").getValue(String.class);
                        String totalConnote = String.valueOf(bagSnapshot.child("totalConnote").getValue(Integer.class));
                        String user = bagSnapshot.child("user").getValue(String.class);
                        String timestamp = bagSnapshot.child("timestamp").getValue(String.class);
                        if (timestamp != null && timestamp.startsWith(selectedDate)) {
                            table.addCell(new PdfPCell(new Phrase(bagId)));
                            table.addCell(new PdfPCell(new Phrase(bagCtx)));
                            table.addCell(new PdfPCell(new Phrase(remarks)));
                            table.addCell(new PdfPCell(new Phrase(origin)));
                            table.addCell(new PdfPCell(new Phrase(totalConnote)));
                            table.addCell(new PdfPCell(new Phrase(user)));
                            table.addCell(new PdfPCell(new Phrase(timestamp)));
                    } else {
                        Log.i("ReportHACB", "No 'bagCtx' data found in snapshot!");
                    }
                    }
                    // Add table to the document
                    document.add(table);
                    int numEmptyParagraphs = 2; // Adjust this value as needed
                    for (int i = 0; i < numEmptyParagraphs; i++) {
                        document.add(new Paragraph(" "));
                    }
                    Paragraph reportSign = new Paragraph("Jakarta, " + formattedDate,sizeFont);
                    reportSign.setAlignment(Element.ALIGN_BOTTOM);
                    reportSign.setAlignment(Element.ALIGN_RIGHT);
                    reportSign.setFont(new Font(Font.FontFamily.HELVETICA, 70, Font.BOLD+Font.BOLD));
                    document.add(reportSign);
                    document.add(new Paragraph(" "));
                    TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
                    String nama = app.getNama();
                    Paragraph reportName = new Paragraph(nama,sizeFont);
                    reportName.setAlignment(Element.ALIGN_BOTTOM);
                    reportName.setAlignment(Element.ALIGN_RIGHT);
                    reportName.setFont(new Font(Font.FontFamily.HELVETICA, 70, Font.BOLD+Font.BOLD));
                    document.add(reportName);
                    // Close the document
                    document.close();
                    outputStream.close();
                    // Show success message
                    Toast.makeText(ReportHACB.this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReportHACB.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
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
                Log.e("ReportHACB", "Error retrieving data", databaseError.toException());
                Toast.makeText(ReportHACB.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openPdf() {
        File file = new File(getExternalFilesDir(null), "report.pdf");
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
            Log.i("ReportHACB", "PDF not found!");
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
        }
    }
    public void showDatePicker() {
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

package com.example.tugasakhir;

import java.util.ArrayList;

public class BagData {
    public String awb;
    public String user;
    public String origin;
    public String facilityCode;
    public String timestamp;
    public ArrayList<String> scannedResults;
    public ArrayList<String> scansBag;
    public ArrayList<String> scansConnote;

    public BagData() {
        // Default constructor diperlukan untuk Firebase Realtime Database
    }

    public BagData(String awb, String user, String origin, String facilityCode, String timestamp,
                   ArrayList<String> scannedResults, ArrayList<String> scansBag, ArrayList<String> scansConnote) {
        this.awb = awb;
        this.user = user;
        this.origin = origin;
        this.facilityCode = facilityCode;
        this.timestamp = timestamp;
        this.scannedResults = scannedResults;
        this.scansBag = scansBag;
        this.scansConnote = scansConnote;
    }
}

package com.example.tugasakhir;

import java.util.ArrayList;

public class BagDataHoBag {

    public String facilityCode;
    public String timestamp;
    public ArrayList<String> scannedResultsHoBag;
    public String indexBag;
    public String user;


    public BagDataHoBag(String indexBag, String user, String facilityCode, String timestamp, ArrayList<String> scannedResultsHoBag) {
        this.indexBag =indexBag;
        this.user =user;
        this.facilityCode=facilityCode;
        this.timestamp=timestamp;
        this.scannedResultsHoBag=scannedResultsHoBag;
    }
}

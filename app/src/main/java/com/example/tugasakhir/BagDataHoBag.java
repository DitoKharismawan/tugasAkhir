package com.example.tugasakhir;

import java.util.ArrayList;

public class BagDataHoBag {

    public String facilityCode;
    public String timestamp;
    public ArrayList<String> gScannedResultsHoBag;
    public String indexBag;
    public String user;
    public String remarks;


    public BagDataHoBag(String indexBag, String user, String facilityCode, String timestamp,String remarks, ArrayList<String> gScannedResultsHoBag) {
        this.indexBag =indexBag;
        this.user =user;
        this.facilityCode=facilityCode;
        this.timestamp=timestamp;
        this. remarks=remarks;
        this.gScannedResultsHoBag=gScannedResultsHoBag;
    }
    public ArrayList<String> getgScannedResultsHoBag() {
        return gScannedResultsHoBag;
    }
}

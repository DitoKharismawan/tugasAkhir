package com.example.tugasakhir;

import java.util.ArrayList;

public class BagDataRevisiBag {
    public String bagRevisiId;
    public String user;
    public String timestamp;
    public String facilityCode;
    public ArrayList<String> gScannedResultsHoBag;
    public BagDataRevisiBag(String bagRevisiId, String user, String timestamp,String facilityCode,  ArrayList<String>gScannedResultsHoBag) {
        this.bagRevisiId=bagRevisiId;
        this.user=user;
        this.timestamp=timestamp;
        this.facilityCode=facilityCode;
        this.gScannedResultsHoBag=gScannedResultsHoBag;

    }
}

package com.example.tugasakhir;

import java.util.ArrayList;
import java.util.HashMap;

public class BagRcvData {
    public String bagRcvId;
    public String user;
    public String timestamp;
    public ArrayList<String> scannedResultsRcvBag;
    public BagRcvData(String bagRcvId, String user, String timestamp,  ArrayList<String> scannedResultsRcvBag) {
        this.bagRcvId=bagRcvId;
        this.user=user;
        this.timestamp=timestamp;
        this.scannedResultsRcvBag=scannedResultsRcvBag;

    }
}

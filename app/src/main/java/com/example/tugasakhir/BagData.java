package com.example.tugasakhir;

import java.util.ArrayList;
import java.util.HashMap;

public class BagData {
    public String awb;
    public String user;
    public String origin;
    public String facilityCode;
    public String timestamp;
    public HashMap<String, ArrayList<String>> bagCtx;



    public BagData(String awb, String user, String origin, String facilityCode, String timestamp, HashMap<String, ArrayList<String>> bagCtx) {
        this.awb = awb;
        this.user = user;
        this.origin = origin;
        this.facilityCode = facilityCode;
        this.timestamp = timestamp;
        this.bagCtx = bagCtx;

    }
}

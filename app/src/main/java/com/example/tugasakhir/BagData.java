package com.example.tugasakhir;

import java.util.ArrayList;
import java.util.HashMap;

public class BagData {
    // Deklarasi atribut-atribut BagData
public String bagId;
    public String awb;
    public String user;
    public String origin;
    public String facilityCode;
    public String remarks;
    public String timestamp;
    public HashMap<String, ArrayList<String>> bagCtx;
    public int totalConnote; // Menambahkan atribut totalConnote

    // Konstruktor untuk BagData
    public BagData(String bagId, String user, String remarks,String origin, String facilityCode, String timestamp, HashMap<String, ArrayList<String>> bagCtx) {
        this.bagId = bagId;
        this.user = user;
        this.origin = origin;
        this.remarks = remarks;
        this.facilityCode = facilityCode;
        this.timestamp = timestamp;
        this.bagCtx = bagCtx;
    }



    // Method untuk menghitung total connote
    public int calculateTotalConnote() {
        int totalConnote = 0;
        if (bagCtx != null) {
            for (ArrayList<String> connotes : bagCtx.values()) {
                totalConnote += connotes.size();
            }
        }
        return totalConnote;
    }

    // Method untuk mengatur nilai totalConnote
    public void setTotalConnote(int totalConnote) {
        this.totalConnote = totalConnote;
    }
}


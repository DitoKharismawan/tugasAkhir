package com.example.tugasakhir;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalDataStore {
    public String awbToBag = "";
    public ArrayList<String> rdbCurrentPath = new ArrayList<>();
    private HashMap<String, ArrayList<String>> bagsHolder = new HashMap<>();
    private ArrayList<String> gScannedResultsHoBag = new ArrayList<>();
    public HashMap<String, ArrayList<String>> getBagsHolder() {
        return bagsHolder;
    }
    public ArrayList<String> getScannedResults() {
        return gScannedResultsHoBag;
    }

    public void setScannedResults(ArrayList<String> scannedResults) {
        this.gScannedResultsHoBag = scannedResults;
    }

    public void clearScannedResults() {
        this.gScannedResultsHoBag.clear();
    }
}

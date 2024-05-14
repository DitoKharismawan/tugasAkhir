package com.example.tugasakhir;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalDataStore {
    public String awbToBag = "";
    public ArrayList<String> rdbCurrentPath = new ArrayList<>();
    private HashMap<String, ArrayList<String>> bagsHolder = new HashMap<>();
    private ArrayList<String> scannedResultsHoBag = new ArrayList<>();
    public HashMap<String, ArrayList<String>> getBagsHolder() {
        return bagsHolder;
    }

    public ArrayList<String> getScannedResultsHoBag() {
        return scannedResultsHoBag;
    }

    public void setScannedResultsHoBag(ArrayList<String> scannedResultsHoBag) {
        this.scannedResultsHoBag = scannedResultsHoBag;
    }

    public void setScannedResults(ArrayList<String> scannedResults) {
        this.scannedResultsHoBag = scannedResults;
    }
}

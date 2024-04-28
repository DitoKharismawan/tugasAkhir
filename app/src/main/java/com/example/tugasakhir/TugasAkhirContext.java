package com.example.tugasakhir;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class TugasAkhirContext extends Application {
    private BagDataStore bagDataStore = new BagDataStore();
    private GlobalConstant globalConstant = new GlobalConstant();

    private GlobalDataStore globalDataStore = new GlobalDataStore();

    public BagDataStore getBagDataStore() {
        return bagDataStore;
    }

    public GlobalConstant getConstant() {
        return globalConstant;
    }

    public GlobalDataStore getGlobalData() { return globalDataStore; }
}

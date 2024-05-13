package com.example.tugasakhir;

import android.app.Application;

public class TugasAkhirContext extends Application {

    private BagDataStore bagDataStore = new BagDataStore();
    private GlobalConstant globalConstant = new GlobalConstant();

    private GlobalDataStore globalDataStore = new GlobalDataStore();
    private GlobalRDBStore globalRDBStore = new GlobalRDBStore();
    private GlobalUtils globalUtils = new GlobalUtils();

    public BagDataStore getBagDataStore() {
        return bagDataStore;
    }

    public GlobalConstant getConstant() {
        return globalConstant;
    }
    public GlobalRDBStore getRDBStore() {
        return globalRDBStore;
    }

    public GlobalUtils getUtils() {
        return globalUtils;
    }

    public GlobalDataStore getGlobalData() { return globalDataStore; }
}

package com.example.tugasakhir;

import android.app.Application;

public class TugasAkhirContext extends Application {
    private String userId;
    private String username;
    private String nama;
    private String origin;
    private String fCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getNama() {return nama;}

    public void setNama(String nama) {this.nama = nama;}

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getFCode() {
        return fCode;
    }

    public void setFCode(String fCode) {
        this.fCode = fCode;
    }
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

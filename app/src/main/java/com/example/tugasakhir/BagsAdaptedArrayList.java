package com.example.tugasakhir;

class BagsAdaptedArrayList {

    private String bagId;
    private int connoteCount;

    public BagsAdaptedArrayList(String bagId, int connoteCount) {
        this.bagId = bagId;
        this.connoteCount = connoteCount;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public int getConnoteCount() {
        return connoteCount;
    }

    public void setConnoteCount(int connoteCount) {
        this.connoteCount = connoteCount;
    }
}

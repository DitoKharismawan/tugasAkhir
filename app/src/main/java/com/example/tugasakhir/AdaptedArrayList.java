package com.example.tugasakhir;

public class AdaptedArrayList {
    private String bagCtx;
    private int connoteCount;
    private int totalConnote; // Tambahkan atribut totalConnote

    public AdaptedArrayList(String bagCtx, int connoteCount, int totalConnote) {
        this.bagCtx = bagCtx;
        this.connoteCount = connoteCount;
        this.totalConnote = totalConnote;
    }



    public String getBagId() {
        return bagCtx;
    }

    public void setBagCtx(String bagCtx) {
        this.bagCtx = bagCtx;
    }

    public int getConnoteCount() {
        return connoteCount;
    }

    public void setConnoteCount(int connoteCount) {
        this.connoteCount = connoteCount;
    }

    public int getTotalConnote() {
        return totalConnote;
    }

    public void setTotalConnote(int totalConnote) {
        this.totalConnote = totalConnote;
    }
}

package com.poupa.attestationdeplacement.dto;

public class Reason {
    String databaseName;
    String qrCodeName;
    boolean enabled = false;
    int x;
    int y;
    int page;

    public Reason(String databaseName, String qrCodeName, int x, int y) {
        this.databaseName = databaseName;
        this.qrCodeName = qrCodeName;
        this.x = x;
        this.y = y;
        this.page = 1;
    }

    public Reason(String databaseName, String qrCodeName, int x, int y, int page) {
        this.databaseName = databaseName;
        this.qrCodeName = qrCodeName;
        this.x = x;
        this.y = y;
        this.page = page;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getQrCodeName() {
        return qrCodeName;
    }

    public void setQrCodeName(String qrCodeName) {
        this.qrCodeName = qrCodeName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}

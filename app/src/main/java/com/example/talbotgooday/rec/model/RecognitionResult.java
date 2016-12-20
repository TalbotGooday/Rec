package com.example.talbotgooday.rec.model;

public class RecognitionResult {
    private String filename;
    private double distance;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public RecognitionResult(String filename, double distance) {
        this.filename = filename;
        this.distance = distance;
    }
}

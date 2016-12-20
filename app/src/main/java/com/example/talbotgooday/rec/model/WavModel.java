package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;

public class WavModel {

    private String fileName;
    private List<Float> wavBytes;
    private List<Float> noLatentList;
    private List<Float> normalizedList;
    private List<List<Float>> spectrum;
    private List<List<Float>> band;

    public WavModel(List<Float> wavBytes, String fileName) {
        this.wavBytes = wavBytes;
        this.fileName = fileName;
    }

    public WavModel(String fileName, List<Float> wavBytes, List<Float> noLatentList,
                    List<Float> normalizedList, List<List<Float>> spectr,
                    List<List<Float>> band) {
        this.fileName = fileName;
        this.wavBytes = wavBytes;
        this.noLatentList = noLatentList;
        this.normalizedList = normalizedList;
        this.spectrum = spectr;
        this.band = band;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<List<Float>> getBand() {
        return band;
    }

    public void setBand(List<List<Float>> band) {
        this.band = band;
    }

    public List<Float> getWavBytes() {
        return wavBytes;
    }

    public List<Float> getNoLatentList() {
        return noLatentList;
    }

    public void setNoLatentList(List<Float> noLatentList) {
        this.noLatentList = new ArrayList<>(noLatentList);
    }

    public List<Float> getNormalizedList() {
        return normalizedList;
    }

    public void setNormalizedList(List<Float> normalizedList) {
        this.normalizedList = new ArrayList<>(normalizedList);
    }

    public List<List<Float>> getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(List<List<Float>> spectrum) {
        this.spectrum = spectrum;
    }

    public void setWavBytes(List<Float> wavBytes) {

        this.wavBytes = wavBytes;
    }

    public WavModel() {
        this.wavBytes = null;
    }

    public WavModel(List<Float> wavBytes) {
        this.wavBytes = wavBytes;
    }
}

package com.example.talbotgooday.rec.data;

import com.example.talbotgooday.rec.model.WavModel;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSet {
    private List<Entry> data;
    private List<Entry> dataBytes;
    private List<Entry> dataNoLatent;
    private List<Entry> dataNormalized;
    private List<List<Entry>> dataSpectrum = new ArrayList<List<Entry>>();
    private List<List<Entry>> dataBand = new ArrayList<List<Entry>>();

    public DataSet() {
        dataBytes = new ArrayList<Entry>();

        for(int i = 0; i < 10; i++)
        {
            Random rnd = new Random();

            int a = rnd.nextInt(i+1);
            int b = rnd.nextInt(i+1);

            dataBytes.add(new Entry(a, b));
        }
    }

    public DataSet(List<Float> wavBytes)
    {
        data = new ArrayList<Entry>();

        for(int i = 0; i < wavBytes.size() - 1; i++)
        {
            data.add(new Entry(i, wavBytes.get(i)));
        }
    }

    public DataSet(WavModel wavModel) {

        dataBytes = new ArrayList<>(getEntries(wavModel.getWavBytes()));
        dataNoLatent = new ArrayList<>(getEntries(wavModel.getNoLatentList()));
        dataNormalized = new ArrayList<>(getEntries(wavModel.getNormalizedList()));

        for (List<Float> spectrum :
                wavModel.getSpectrum()) {
            dataSpectrum.add(getEntries(spectrum));
        }

        for (List<Float> band :
                wavModel.getBand()) {
            dataBand.add(getEntries(band));
        }
    }

    public List<Entry> getDataBytes() {
        return dataBytes;
    }

    public List<Entry> getDataNoLatent() {
        return dataNoLatent;
    }

    public List<Entry> getDataNormalized() {
        return dataNormalized;
    }

    public List<List<Entry>> getDataSpectrum() {
        return dataSpectrum;
    }

    public List<List<Entry>> getDataBand() {
        return dataBand;
    }

    private List<Entry> getEntries(List<Float> tmpData){
        List<Entry> data = new ArrayList<>();

        for(int i = 0; i < tmpData.size() - 1; i++)
        {
            data.add(new Entry(i, tmpData.get(i)));
        }

        return data;
    }

    public List<Entry> getData() {
        return data;
    }
}

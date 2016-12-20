package com.example.talbotgooday.rec.service;

import com.example.talbotgooday.rec.model.WavModel;

import java.io.IOException;
import java.util.List;

public interface WavModelService {

    List<Float> load(String filePath) throws IOException;

    List<WavModel> loadEtalons();

    List<Float> delLatentPeriod(WavModel wavModel);

    List<Float> normalize(WavModel wavModel);

    List<List<Float>> spectrumLineView(WavModel wavModel);

    List<Float> getLowPassFilterData(List<Float> band, int N, double fCP);

    List<Float> envelopExtracting(List<Float> wavBayts);
}

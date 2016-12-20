package com.example.talbotgooday.rec.service;

import com.example.talbotgooday.rec.model.LowPassFilter;
import com.example.talbotgooday.rec.model.WavModel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class WavModelServiceImpl implements WavModelService {
    private int USELESS_BYTE_COUNT = 43;
    private int[] BAND_SPECTRAL_DECOMPOSITION = {0, 2, 4, 6, 8, 10, 15, 25, 50, 128};
    private int[] BAND_SPECTRAL_DECOMPOSITION_Chebyshev = {0, 4, 8, 12, 16, 20, 30, 50, 100, 256};
    private double min = 0;

    @Override
    public List<Float> load(String filePath) throws IOException {
        List<Float> wavBytes = new ArrayList<>();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0) {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();
        int len = audioBytes.length;

        for (int i = USELESS_BYTE_COUNT; i < len-1; i += 2) {
            wavBytes.add(new BigInteger(new byte[]{audioBytes[i], audioBytes[i + 1]}).floatValue());
        }

        return wavBytes;
    }

    @Override
    public List<WavModel> loadEtalons() {
        /*OpenFileDialog dialog = new OpenFileDialog();
        List<ArrayList<Object>> wavBytesEtalons= new ArrayList<>();*/
        List<WavModel> etalons = new ArrayList<>();
        /*int etalonSize =0;
        List<String> path = new ArrayList<>();
        for (File f : dialog.getAllFiles()) {
            wavBytesEtalons.add(new ArrayList<>());
            etalonSize = wavBytesEtalons.size();
            try (RandomAccessFile data = new RandomAccessFile(new File(f.getPath()), "r")) {
                byte[] eight;
                data.readFully(eight = new byte[USELESS_BYTE_COUNT]);
                for (long i = 0, len = (data.length() - USELESS_BYTE_COUNT) / 2; i < len; i++) {
                    data.readFully(eight = new byte[2]);
                    wavBytesEtalons.get(etalonSize-1).add(new BigInteger(eight).doubleValue());
                }
                data.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            etalons.add(new WavModel(wavBytesEtalons.get(etalonSize-1),f.getName()));
        }*/
        return etalons;

    }

    @Override
    public List<Float> delLatentPeriod(WavModel wavModel) {
        float threshold = MathHelper.threshold(wavModel.getWavBytes());
        List<Float> bytes = new ArrayList<>(wavModel.getWavBytes());
        boolean cutOnBegin = true,
                cutOnEnd = true;
        while (cutOnBegin || cutOnEnd) {

            if (Math.abs(bytes.get(0)) < threshold) {
                bytes.remove(0);
            } else {
                cutOnBegin = false;
                if (bytes.get(bytes.size() - 1) < threshold) {
                    bytes.remove(bytes.size() - 1);
                } else {
                    cutOnEnd = false;
                    break;

                }
            }
        }

        return bytes;
    }

    @Override
    public List<Float> normalize(WavModel wavModel) {
        List<Float> bytes = new ArrayList<>(wavModel.getNoLatentList());
        float disp = MathHelper.dispersion(bytes);
        for (int i = 0; i < bytes.size(); i++) {
            float value = bytes.get(i);
            bytes.set(i, (value / (disp)));
        }
        return bytes;
    }

    @Override
    public List<List<Float>> spectrumLineView(WavModel wavModel) {
        List<List<Float>> lineSpectrum = new ArrayList<>();
        int rowsCount = wavModel.getSpectrum().size();
        int[] bandDecomposition;

        for (int band = 0; band < BAND_SPECTRAL_DECOMPOSITION.length - 1; band++) {
            lineSpectrum.add(new ArrayList<Float>());
            for (int i = 0; i < rowsCount; i++) {

                float value = 0;
                bandDecomposition = BAND_SPECTRAL_DECOMPOSITION;

                if (wavModel.getSpectrum().get(0).size() > 128) {
                    bandDecomposition = BAND_SPECTRAL_DECOMPOSITION_Chebyshev;
                }
                for (int j = bandDecomposition[band]; j < bandDecomposition[band + 1]; j++) {
                    value += wavModel.getSpectrum().get(i).get(j);
                }
                lineSpectrum.get(lineSpectrum.size() - 1).add(value);
            }
        }
        return lineSpectrum;
    }


    @Override
    public List<Float> getLowPassFilterData(List<Float> band, int N, double fCP) {
        LowPassFilter lpf = new LowPassFilter(N, fCP);

        return lpf.applyLPfilter(band);
    }

    @Override
    public List<Float> envelopExtracting(List<Float> wavBayts) {
        LowPassFilter lpf = new LowPassFilter(0, 0);
        List<Float> absoluteWavBytes = new ArrayList<>();
        for (float b : wavBayts) {
            absoluteWavBytes.add(Math.abs(b));
        }
        return lpf.applyLPfilter2(absoluteWavBytes);

    }
}

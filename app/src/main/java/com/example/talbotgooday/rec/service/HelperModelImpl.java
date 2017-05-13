package com.example.talbotgooday.rec.service;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.talbotgooday.rec.R;
import com.example.talbotgooday.rec.model.WavModel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HelperModelImpl implements HelperModel {
    private static final int USELESS_BYTE_COUNT = 43;

    @Override
    public void swapFragment(FragmentManager manager, Fragment fragment, Bundle bundle) {
        if (fragment != null) {

            if (bundle != null) fragment.setArguments(bundle);

            manager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }
    }

    @Override
    public void deleteFragment(FragmentManager manager, int id) {
        manager.beginTransaction().
                remove(manager.findFragmentById(id)).commit();
    }

    @Override
    public ArrayList<String> getZipFilesNames(String zipName) {
        ArrayList<String> result = new ArrayList<>();

        ZipInputStream zin;
        try {
            zin = new ZipInputStream(new FileInputStream(zipName));

            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                result.add(entry.getName());
                zin.closeEntry();
            }
            zin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<WavModel> getZipFileBytesData(String zipName) throws IOException {
        List<Float> tmpResult;
        List<WavModel> result = new ArrayList<>();

        ZipInputStream zin;
        ByteArrayOutputStream out;

        int read;
        byte[] buff = new byte[1024];

        zin = new ZipInputStream(new FileInputStream(zipName));

        WavModel wavModel;
        ZipEntry entry;

        while ((entry = zin.getNextEntry()) != null) {
            out = new ByteArrayOutputStream();

            while ((read = zin.read(buff)) > 0) {
                out.write(buff, 0, read);
            }

            out.flush();
            byte[] audioBytes = out.toByteArray();
            int len = audioBytes.length;

            tmpResult = new ArrayList<>();

            for (int i = USELESS_BYTE_COUNT; i < len - 1; i += 2) {
                tmpResult.add(new BigInteger(new byte[]{audioBytes[i], audioBytes[i + 1]}).floatValue());
            }

            wavModel = new WavModel(tmpResult, entry.getName());
            result.add(wavModel);

            zin.closeEntry();
        }
        zin.close();

        return result;
    }
}

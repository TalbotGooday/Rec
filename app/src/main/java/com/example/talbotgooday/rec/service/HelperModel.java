package com.example.talbotgooday.rec.service;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.talbotgooday.rec.model.WavModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface HelperModel {
    void swapFragment(FragmentManager manager, Fragment fragment, Bundle bundle);

    void deleteFragment(FragmentManager manager, int id);

    ArrayList<String> getZipFilesNames(String zipName);

    List<WavModel> getZipFileBytesData(String zipName) throws IOException;
}

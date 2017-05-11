package com.example.talbotgooday.rec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.talbotgooday.rec.adapters.ItemsAdapter;
import com.example.talbotgooday.rec.service.HelperModel;
import com.example.talbotgooday.rec.service.HelperModelImpl;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class WavChooseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cards_holder, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerView mRecyclerView = ButterKnife.findById(rootView, R.id.rv_charts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false
        ));

        Bundle bundle = getArguments();
        String path = bundle.getString("fileListPath");

        HelperModel helper = new HelperModelImpl();

        ArrayList<String> data = helper.getZipFilesNames(path);

        if (data.size() == 0)
            Toast.makeText(getContext(), getContext().getString(R.string.err_file_not_found), Toast.LENGTH_SHORT).show();

        ItemsAdapter adapter = new ItemsAdapter(data, bundle);
        mRecyclerView.setAdapter(adapter);
        return rootView;
    }
}

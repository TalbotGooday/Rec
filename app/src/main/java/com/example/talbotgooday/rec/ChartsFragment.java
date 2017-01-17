package com.example.talbotgooday.rec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.talbotgooday.rec.adapters.ChartsAdapter;
import com.example.talbotgooday.rec.model.Chebyshev;
import com.example.talbotgooday.rec.model.FFT;
import com.example.talbotgooday.rec.model.WavModel;
import com.example.talbotgooday.rec.service.WavModelService;
import com.example.talbotgooday.rec.service.WavModelServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ChartsFragment extends Fragment {

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
        String wavPath = bundle.getString("wavPath");
        int spectrumType = bundle.getInt("spectrum");

        String fileShortName = "";

        if (wavPath != null) {
            fileShortName = wavPath.substring(wavPath.lastIndexOf("/") + 1);
        }

        List<Float> wavBytes = new ArrayList<>();

        WavModelService service = new WavModelServiceImpl();

        try {
            wavBytes = service.load(wavPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (wavBytes.size() > 0) {
            WavModel data = new WavModel(new ArrayList<>(wavBytes), fileShortName);
            data.setNoLatentList(service.delLatentPeriod(data));
            data.setNormalizedList(service.normalize(data));

            if (spectrumType == 0) {
                data.setSpectrum(new FFT().fff(data));
            } else data.setSpectrum(new Chebyshev().getChebyshevResult(data));

            data.setBand(service.spectrumLineView(data));

            ChartsAdapter adapter = new ChartsAdapter(data);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int pos = linearLayoutManager1.findFirstVisibleItemPosition();

                    String[] titles = getContext().getResources().getStringArray(R.array.charts_names);
                    if (pos >= 0 && pos <= titles.length) {
                        String title = titles[pos];
                        getActivity().setTitle(title);
                    }
                }
            });
        }

        return rootView;
    }
}

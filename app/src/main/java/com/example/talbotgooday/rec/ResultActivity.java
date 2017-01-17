package com.example.talbotgooday.rec;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.talbotgooday.rec.adapters.ChartsAdapter;
import com.example.talbotgooday.rec.model.Chebyshev;
import com.example.talbotgooday.rec.model.DTW;
import com.example.talbotgooday.rec.model.FFT;
import com.example.talbotgooday.rec.model.ResultModel;
import com.example.talbotgooday.rec.model.WavModel;
import com.example.talbotgooday.rec.service.HelperModel;
import com.example.talbotgooday.rec.service.HelperModelImpl;
import com.example.talbotgooday.rec.service.WavModelService;
import com.example.talbotgooday.rec.service.WavModelServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.rv_charts)
    RecyclerView mRecyclerView;

    private static final String SPECTRUM = "spectrum";
    private static final String POSITION = "itemPos";
    private static final String PATH = "fileListPath";
    private static final String DIALOG = "dialog";

    private List<WavModel> mModels = new ArrayList<>();
    private int mChosenPos = -1;
    private int mSpectrumType;
    private WavModel mChosenModel;
    private List<ResultModel> mRecResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        LoadTask task = new LoadTask(this);
        task.execute(bundle);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        ));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();

                int pos = linearLayoutManager1.findFirstVisibleItemPosition();

                String[] titles = getResources().getStringArray(R.array.charts_names);
                if (pos >= 0 && pos <= titles.length) {
                    String title = titles[pos];
                    setTitle(title);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recognise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.itm_recognise_result) {
            if (mRecResult == null) {
                RecogniseTask task = new RecogniseTask(this);
                task.execute();
            } else {
                showResultDialog(mRecResult);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadTask extends AsyncTask<Bundle, Void, ChartsAdapter> {
        private ProgressDialog pd;
        private Context context;

        LoadTask(Context mContext) {
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Подождите немного");
            pd.show();
        }

        @Override
        protected ChartsAdapter doInBackground(Bundle... params) {

            Bundle bundle = params[0];
            WavModel data = null;

            try {
                HelperModel presenter = new HelperModelImpl();
                String zipPath = getIntent().getExtras().getString(PATH);
                mModels = presenter.getZipFileBytesData(zipPath);

                mSpectrumType = bundle.getInt(SPECTRUM);
                mChosenPos = bundle.getInt(POSITION);


                WavModelService service = new WavModelServiceImpl();

                data = mModels.get(mChosenPos);

                if (data != null) {
                    data.setNoLatentList(service.delLatentPeriod(data));
                    data.setNormalizedList(service.normalize(data));

                    if (mSpectrumType == 0) {
                        data.setSpectrum(new FFT().fff(data));
                    } else data.setSpectrum(new Chebyshev().getChebyshevResult(data));

                    data.setBand(service.spectrumLineView(data));

                    mChosenModel = data;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ChartsAdapter adapter = null;
            if (data != null)
                adapter = new ChartsAdapter(data);

            return adapter;
        }

        @Override
        protected void onPostExecute(ChartsAdapter result) {
            super.onPostExecute(result);
            pd.dismiss();

            mRecyclerView.setAdapter(result);
        }
    }

    private class RecogniseTask extends AsyncTask<Void, Void, List<ResultModel>> {
        private ProgressDialog pd;
        private Context context;

        RecogniseTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage(getResources().getString(R.string.txt_wait));
            pd.show();
        }

        @Override
        protected List<ResultModel> doInBackground(Void... params) {
            List<ResultModel> result = new ArrayList<>();
            WavModelService service = new WavModelServiceImpl();

            for (int i = 0; i < mModels.size(); i++) {
                if (i != mChosenPos) {
                    WavModel data = mModels.get(i);

                    if (data != null) {
                        data.setNoLatentList(service.delLatentPeriod(data));
                        data.setNormalizedList(service.normalize(data));

                        if (mSpectrumType == 0) {
                            data.setSpectrum(new FFT().fff(data));
                        } else data.setSpectrum(new Chebyshev().getChebyshevResult(data));

                        data.setBand(service.spectrumLineView(data));

                        DTW dtw = new DTW(mChosenModel.getBand(), data.getBand());

                        result.add(new ResultModel(data.getFileName(), dtw.getDistance()));
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<ResultModel> result) {
            super.onPostExecute(result);
            pd.dismiss();
            mRecResult = result;

            showResultDialog(result);
        }
    }

    private void showResultDialog(List<ResultModel> result) {
        RecogniseResultDialog dialog;
        dialog = RecogniseResultDialog.newInstance(result);
        String spectrumName = mSpectrumType == 0 ?
                getResources().getString(R.string.fourer_spectrum)
                :
                getResources().getString(R.string.chebyshev_spectrum);

        dialog.setTitle(String.format("%s: %s", mModels.get(mChosenPos).getFileName(), spectrumName));
        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        dialog.show(transaction, DIALOG);
    }
}

package com.example.talbotgooday.rec;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.talbotgooday.rec.model.ChartsDataModel;
import com.example.talbotgooday.rec.model.Chebyshev;
import com.example.talbotgooday.rec.model.DTW;
import com.example.talbotgooday.rec.model.FFT;
import com.example.talbotgooday.rec.model.ResultModel;
import com.example.talbotgooday.rec.model.WavModel;
import com.example.talbotgooday.rec.service.HelperModel;
import com.example.talbotgooday.rec.service.HelperModelImpl;
import com.example.talbotgooday.rec.service.WavModelService;
import com.example.talbotgooday.rec.service.WavModelServiceImpl;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultTabbedActivity extends AppCompatActivity {

    private static final String SPECTRUM = "spectrum";
    private static final String POSITION = "itemPos";
    private static final String PATH = "fileListPath";
    private static final String DIALOG = "dialog";
    private static final String MENU = "isMenuEnabled";

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.toolbar_result)
    Toolbar mToolbar;

    private String[] mTitles;

    private List<WavModel> mWavModels = new ArrayList<>();
    private int mChosenWavIndex = -1;
    private int mSpectrumType;
    private WavModel mChosenModel;
    private List<ResultModel> mRecResult;

    private boolean isMenuEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tabbed);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mTitles = getResources().getStringArray(R.array.charts_names);

        Bundle bundle = getIntent().getExtras();
        isMenuEnabled = bundle.getBoolean(MENU);

        LoadTask task = new LoadTask(this);
        task.execute(bundle);


    }

    private void setViewPager() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(mTitles[position]);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMenuEnabled)
            getMenuInflater().inflate(R.menu.menu_result_tabbed, menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mTitles[0]);
        }

        mToolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToast();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itm_recognise_result:
                if (mRecResult == null) {
                    RecogniseTask task = new RecogniseTask(this);
                    task.execute();
                } else {
                    showResultDialog(mRecResult);
                }
                break;

            case android.R.id.home:
                this.finish();
                break;
        }

        return false;
    }

    public static class PlaceholderFragment extends Fragment {
        private LineChart mChart;
        private FrameLayout mSignalSpectrumLayout;
        private SeekBar mSeekBarChart;
        private LinearLayout mStripesLayout;
        private EditText mStripesNEdt;
        private EditText mStripesFCPEdt;
        private Button mBtnBand;

        private static WavModel mWavModel;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber, WavModel chosenModel) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            mWavModel = chosenModel;

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        private void setViewsCharacteristics(int position) {
            switch (position) {
                case 0:
                    mSignalSpectrumLayout.setVisibility(View.GONE);
                    mStripesLayout.setVisibility(View.GONE);
                    break;

                case 1:
                    mSignalSpectrumLayout.setVisibility(View.GONE);
                    mStripesLayout.setVisibility(View.GONE);
                    break;

                case 2:
                    mSignalSpectrumLayout.setVisibility(View.GONE);
                    mStripesLayout.setVisibility(View.GONE);
                    break;

                case 3:
                    mSignalSpectrumLayout.setVisibility(View.VISIBLE);
                    mStripesLayout.setVisibility(View.GONE);
                    break;

                case 4:
                    mSignalSpectrumLayout.setVisibility(View.VISIBLE);
                    mStripesLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private LineDataSet getData(int id, int pos, WavModel model) {
            List<Entry> data = new ArrayList<>();

            ChartsDataModel analyser = new ChartsDataModel(model);

            switch (id) {
                case 0:
                    data = analyser.getDataBytes();
                    break;

                case 1:
                    data = analyser.getDataNoLatent();
                    break;

                case 2:
                    data = analyser.getDataNormalized();
                    break;

                case 3:
                    data = analyser.getDataSpectrum().get(pos);
                    break;

                case 4:
                    data = analyser.getDataBand().get(pos);
                    break;

            }

            LineDataSet dataSet = new LineDataSet(data, String.valueOf(id));
            dataSet.setDrawCircles(false);
            dataSet.setLabel(String.valueOf(pos));

            return dataSet;
        }

        private LineDataSet getNewLineDataSet(List<Float> data, int pos) {
            List<Entry> toPointsData = new ChartsDataModel(data).getData();

            LineDataSet dataSet = new LineDataSet(toPointsData, String.valueOf(pos));
            dataSet.setDrawCircles(false);
            dataSet.setLabel(String.valueOf(pos));
            dataSet.setColor(Color.YELLOW);

            return dataSet;
        }

        private void bindViews(View rootView) {
            mChart = ButterKnife.findById(rootView, R.id.chart);
            mSignalSpectrumLayout = ButterKnife.findById(rootView, R.id.signal_spectrum);
            mSeekBarChart = ButterKnife.findById(rootView, R.id.seek_bar_chart);
            mStripesLayout = ButterKnife.findById(rootView, R.id.stripes);
            mStripesNEdt = ButterKnife.findById(rootView, R.id.edt_n_stripes);
            mStripesFCPEdt = ButterKnife.findById(rootView, R.id.edt_fcp_stripes);
            mBtnBand = ButterKnife.findById(rootView, R.id.btn_band);

        }

        private void loadData(final int position) {
            final ChartsDataModel analyser = new ChartsDataModel(mWavModel);
            int chartDataCount = analyser.getDataSpectrum().size();

            switch (position) {
                case 3:
                    chartDataCount = analyser.getDataSpectrum().size();
                    break;

                case 4:
                    chartDataCount = analyser.getDataBand().size();
                    break;
            }

            mSeekBarChart.setMax(chartDataCount - 1);

            mSeekBarChart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                private int pos = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pos = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    LineData lineData = new LineData(getData(position, pos, mWavModel));

                    if (position == 4) {
                        if (lineData.getDataSetCount() < 2) {
                            WavModelService service = new WavModelServiceImpl();
                            int n = 0;
                            double fcp = 0;
                            int pos = mSeekBarChart.getProgress();

                            try {
                                fcp = Float.valueOf(String.valueOf(mStripesFCPEdt.getText()));
                                n = Integer.parseInt(String.valueOf(mStripesNEdt.getText()));
                            } catch (Exception ignored) {
                            }

                            List<Float> filter = service.getLowPassFilterData(
                                    mWavModel.getBand().get(pos), n, fcp);


                            lineData.addDataSet(getNewLineDataSet(filter, pos));
                        }
                    }

                    mChart.setData(lineData);
                    mChart.invalidate();

                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                }
            });

            mBtnBand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WavModelService service = new WavModelServiceImpl();
                    int n = 0;
                    float fcp = 0.2f;
                    int pos = mSeekBarChart.getProgress();

                    LineData lineData = new LineData(getData(position, pos, mWavModel));

                    try {
                        fcp = Float.valueOf(String.valueOf(mStripesFCPEdt.getText()));
                        n = Integer.parseInt(String.valueOf(mStripesNEdt.getText()));
                    } catch (Exception ignored) {
                    }

                    List<Float> filter = service.getLowPassFilterData(
                            mWavModel.getBand().get(pos), n, fcp);


                    lineData.addDataSet(getNewLineDataSet(filter, pos));


                    mChart.setData(lineData);
                    mChart.invalidate();

                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                }
            });

            LineData lineData = new LineData(getData(position, 0, mWavModel));

            Description description = new Description();
            description.setText(String.format(Locale.getDefault(), "Количество элементов: %d",
                    lineData.getEntryCount() + 1));


            mChart.setData(lineData);
            mChart.setDescription(description);
            mChart.invalidate();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.card_chart, container, false);

            final int pos = getArguments().getInt(ARG_SECTION_NUMBER);

            bindViews(rootView);
            setViewsCharacteristics(pos);

            LoadData loadData = new LoadData();
            loadData.execute(pos);

            return rootView;
        }

        private class LoadData extends AsyncTask<Integer, Void, Void> {
            @Override
            protected Void doInBackground(Integer... params) {
                final int position = params[0];


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadData(position);
                    }
                });


                return null;
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, mChosenModel);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= mTitles.length) return null;
            else return mTitles[position];
        }
    }

    private class LoadTask extends AsyncTask<Bundle, Void, WavModel> {
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
        protected WavModel doInBackground(Bundle... params) {
            Bundle bundle = params[0];
            WavModel data = null;

            try {
                HelperModel presenter = new HelperModelImpl();
                String path = getIntent().getExtras().getString(PATH);
                mSpectrumType = bundle.getInt(SPECTRUM);
                mChosenWavIndex = bundle.getInt(POSITION);
                WavModelService service = new WavModelServiceImpl();

                if (mChosenWavIndex != -1) {
                    mWavModels = presenter.getZipFileBytesData(path);
                    data = mWavModels.get(mChosenWavIndex);
                } else {
                    String fileShortName = path.substring(path.lastIndexOf("/") + 1);
                    data = new WavModel(new ArrayList<>(service.load(path)), fileShortName);
                }

                if (data != null) {
                    data.setNoLatentList(service.delLatentPeriod(data));
                    data.setNormalizedList(service.normalize(data));

                    if (mSpectrumType == 0) {
                        data.setSpectrum(new FFT().fff(data));
                    } else
                        data.setSpectrum(new Chebyshev().getChebyshevResult(data));

                    data.setBand(service.spectrumLineView(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(WavModel result) {
            super.onPostExecute(result);
            pd.dismiss();

            mChosenModel = result;
            setViewPager();
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

            for (int i = 0; i < mWavModels.size(); i++) {
                if (i != mChosenWavIndex) {
                    WavModel data = mWavModels.get(i);

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

        dialog.setTitle(String.format("%s: %s", mWavModels.get(mChosenWavIndex).getFileName(), spectrumName));
        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        dialog.show(transaction, DIALOG);
    }

    private void showToast() {
        Toast.makeText(this, mToolbar.getTitle(), Toast.LENGTH_LONG).show();
    }
}

package com.example.talbotgooday.rec.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.talbotgooday.rec.R;
import com.example.talbotgooday.rec.model.ChartsDataModel;
import com.example.talbotgooday.rec.model.WavModel;
import com.example.talbotgooday.rec.service.WavModelService;
import com.example.talbotgooday.rec.service.WavModelServiceImpl;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartsAdapter extends RecyclerView.Adapter<ChartsAdapter.ViewHolder> {
    private WavModel mDataModel;

    private Context mContext;

    public ChartsAdapter(WavModel mDataModel) {
        this.mDataModel = mDataModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChartsDataModel analyser = new ChartsDataModel(mDataModel);
        int chartDataCount = analyser.getDataSpectrum().size();

        switch (position) {
            case 0:
                holder.signalSpectrumLayout.setVisibility(View.GONE);
                holder.stripesLayout.setVisibility(View.GONE);
                break;

            case 1:
                holder.signalSpectrumLayout.setVisibility(View.GONE);
                holder.stripesLayout.setVisibility(View.GONE);
                break;

            case 2:
                holder.signalSpectrumLayout.setVisibility(View.GONE);
                holder.stripesLayout.setVisibility(View.GONE);
                break;

            case 3:
                chartDataCount = analyser.getDataSpectrum().size();

                holder.signalSpectrumLayout.setVisibility(View.VISIBLE);
                holder.stripesLayout.setVisibility(View.GONE);
                break;

            case 4:
                chartDataCount = analyser.getDataBand().size();

                holder.signalSpectrumLayout.setVisibility(View.VISIBLE);
                holder.stripesLayout.setVisibility(View.VISIBLE);
                break;
        }

        holder.seekBarChart.setMax(chartDataCount - 1);
        holder.seekBarChart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                LineData lineData = new LineData(getData(holder.getAdapterPosition(), pos));

                if (holder.getAdapterPosition() == 4) {
                    if (lineData.getDataSetCount() < 2) {
                        WavModelService service = new WavModelServiceImpl();
                        int n = 0;
                        double fcp = 0;
                        int pos = holder.seekBarChart.getProgress();

                        try {
                            fcp = Float.valueOf(String.valueOf(holder.stripesFCPEdt.getText()));
                            n = Integer.parseInt(String.valueOf(holder.stripesNEdt.getText()));
                        } catch (Exception ignored) {
                        }

                        List<Float> filter = service.getLowPassFilterData(
                                mDataModel.getBand().get(pos), n, fcp);


                        lineData.addDataSet(getNewLineDataSet(filter, pos));
                    }
                }

                holder.chart.setData(lineData);

                holder.chart.invalidate();

                holder.chart.notifyDataSetChanged();
                holder.chart.invalidate();
            }
        });

        holder.btnBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WavModelService service = new WavModelServiceImpl();
                int n = 0;
                float fcp = 0.2f;
                int pos = holder.seekBarChart.getProgress();

                LineData lineData = new LineData(getData(holder.getAdapterPosition(), pos));

                try {
                    fcp = Float.valueOf(String.valueOf(holder.stripesFCPEdt.getText()));
                    n = Integer.parseInt(String.valueOf(holder.stripesNEdt.getText()));
                } catch (Exception ignored) {
                }

                List<Float> filter = service.getLowPassFilterData(
                        mDataModel.getBand().get(pos), n, fcp);


                lineData.addDataSet(getNewLineDataSet(filter, pos));


                holder.chart.setData(lineData);

                holder.chart.invalidate();

                holder.chart.notifyDataSetChanged();
                holder.chart.invalidate();
            }
        });

        LineData lineData = new LineData(getData(position, 0));

        holder.chart.setData(lineData);

        holder.chart.invalidate();
    }

    private LineDataSet getData(int id, int pos) {
        List<Entry> data = new ArrayList<>();

        ChartsDataModel analyser = new ChartsDataModel(mDataModel);

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

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chart)
        LineChart chart;

        @BindView(R.id.signal_spectrum)
        FrameLayout signalSpectrumLayout;

        @BindView(R.id.seek_bar_chart)
        SeekBar seekBarChart;

        @BindView(R.id.stripes)
        LinearLayout stripesLayout;

        @BindView(R.id.edt_n_stripes)
        EditText stripesNEdt;

        @BindView(R.id.edt_fcp_stripes)
        EditText stripesFCPEdt;

        @BindView(R.id.btn_band)
        Button btnBand;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

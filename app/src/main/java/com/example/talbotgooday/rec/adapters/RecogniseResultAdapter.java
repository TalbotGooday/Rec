package com.example.talbotgooday.rec.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.talbotgooday.rec.R;
import com.example.talbotgooday.rec.model.ResultModel;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecogniseResultAdapter extends RecyclerView.Adapter<RecogniseResultAdapter.ViewHolder> {

    private List<ResultModel> mData;
    private Context mContext;

    public RecogniseResultAdapter(List<ResultModel> mData, Context mContext) {
        Collections.sort(mData, new ResultModel.ResultComparator());
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_result_item, parent, false);
        return new RecogniseResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResultModel item = mData.get(position);

        holder.name.setText(item.getName());
        holder.value.setText(item.getValueString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_name_result)
        TextView name;

        @BindView(R.id.txt_value_result)
        TextView value;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}


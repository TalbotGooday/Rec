package com.example.talbotgooday.rec.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.talbotgooday.rec.R;
import com.example.talbotgooday.rec.ResultTabbedActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mData;
    private Bundle mBundle;

    public ItemsAdapter(List<String> mData, Bundle mBundle) {
        this.mData = mData;
        this.mBundle = mBundle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_wav_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.text.setText(mData.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ResultTabbedActivity.class);
                mBundle.putInt("itemPos", holder.getAdapterPosition());
                mBundle.putBoolean("isMenuEnabled", true);

                intent.putExtras(mBundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)
        TextView text;

        @BindView(R.id.card_item)
        RelativeLayout card;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

package com.example.talbotgooday.rec;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.talbotgooday.rec.adapters.RecogniseResultAdapter;
import com.example.talbotgooday.rec.model.ResultModel;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;

public class RecogniseResultDialog extends DialogFragment {
    private static final String ITEMS = "items";
    private String mTitle;

    public static RecogniseResultDialog newInstance(List<ResultModel> items) {
        RecogniseResultDialog resultDialog = new
                RecogniseResultDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);

        resultDialog.setArguments(args);

        return resultDialog;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_cards_holder, null);

        ButterKnife.bind(rootView);

        Context context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);
        builder.setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        String strTitle = mTitle == null ? context.getString(R.string.app_name) : mTitle;

        builder.setTitle(strTitle);

        List<ResultModel> data = (List) getArguments().getSerializable(ITEMS);

        RecogniseResultAdapter adapter = new RecogniseResultAdapter(data, context);

        RecyclerView recyclerView = ButterKnife.findById(rootView, R.id.rv_charts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false
        ));

        recyclerView.setAdapter(adapter);

        return builder.create();
    }
}

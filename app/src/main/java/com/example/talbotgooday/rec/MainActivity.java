package com.example.talbotgooday.rec;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talbotgooday.rec.service.HelperModel;
import com.example.talbotgooday.rec.service.HelperModelImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 1;
    private static final int ARCHIVE_SELECT_CODE = 2;
    private Bundle mBundle = new Bundle();
    private HelperModel mHelper;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.txt_empty_content)
    TextView mEmptyContentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mHelper = new HelperModelImpl();

        mBundle.putInt("spectrum", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_load_wav:
                showFileChooser(FILE_SELECT_CODE);
                break;
            case R.id.action_load_all:
                showFileChooser(ARCHIVE_SELECT_CODE);
                break;

            case R.id.itm_fourer_spectrum:
                mBundle.putInt("spectrum", 0);
                if (!item.isChecked())
                    item.setChecked(true);
                break;

            case R.id.itm_chebyshev_spectrum:
                mBundle.putInt("spectrum", 1);
                if (!item.isChecked())
                    item.setChecked(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser(int selectCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        switch (selectCode) {
            case FILE_SELECT_CODE:
                intent.setType("audio/*");
                break;

            case ARCHIVE_SELECT_CODE:
                intent.setType("application/zip");
                break;
        }

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    selectCode);
        } catch (ActivityNotFoundException ex) {

            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(this, uri);
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

                    mBundle.putString("wavPath", path);
                    setFragment(0);

                }
                break;

            case ARCHIVE_SELECT_CODE:
                Uri uri = data.getData();
                String path = getPath(this, uri);

                mBundle.putString("fileListPath", path);

                setFragment(1);

                break;

            case RESULT_CANCELED:
                break;
        }
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = -1;
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow("_data");
                }
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }

                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception ignored) {
            }

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private void setFragment(int index) {
        Fragment fragment = index == 0 ? new ChartsFragment() : new WavChooseFragment();

        mEmptyContentText.setVisibility(View.INVISIBLE);

        mHelper.swapFragment(getSupportFragmentManager(), fragment, mBundle);
    }
}

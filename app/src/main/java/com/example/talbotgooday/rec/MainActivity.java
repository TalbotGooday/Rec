package com.example.talbotgooday.rec;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 1;
    private static final int ARCHIVE_SELECT_CODE = 2;
    private Bundle mBundle = new Bundle();

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setFragment();
                Snackbar.make(view, "СУК", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                if(!item.isChecked())
                    item.setChecked(true);
                break;

            case R.id.itm_chebyshev_spectrum:
                mBundle.putInt("spectrum", 1);
                if(!item.isChecked())
                    item.setChecked(true);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser(int selectCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        switch (selectCode) {
            case FILE_SELECT_CODE:
                //intent.setType("audio/wav");
                intent.setType("audio/*");
                break;

            case ARCHIVE_SELECT_CODE:
                intent.setType("application/zip");
                break;
        }

        //intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    selectCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
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
                    try {
                        String path = getPath(this, uri);
                        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

                        //MenuItem item = (MenuItem) findViewById(R.id.action_load_all);
                        setFragment(path);

                        /*if (!item.isEnabled())
                            item.setEnabled(true);*/
                    } catch (URISyntaxException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case ARCHIVE_SELECT_CODE:
                Uri uri = data.getData();
                String Fpath = null;
                try {
                    Fpath = getPath(this, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                int i = -1;
                try {
                    if (Fpath != null)
                        i = readZip(Fpath);
                    Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;

            case RESULT_CANCELED:
                break;
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = -1;
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow("_data");
                }
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private int readZip(String zipName) throws IOException {
        int counter = 0;
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipName));
        //ZipEntry entry;
        while (zin.getNextEntry() != null) {
            //анализ entry
            //считывание содежимого
            counter++;
            zin.closeEntry();
        }
        zin.close();

        return counter;
    }

    private void setFragment(String wavPath) {
        mEmptyContentText.setVisibility(View.INVISIBLE);

        mBundle.putString("wavPath", wavPath);

        Fragment fragment = new ChartsFragment();
        fragment.setArguments(mBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

    }
}

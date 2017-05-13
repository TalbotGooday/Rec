package com.example.talbotgooday.rec;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talbotgooday.rec.service.HelperModel;
import com.example.talbotgooday.rec.service.HelperModelImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FILE_SELECT_CODE = 1;
    private static final int ARCHIVE_SELECT_CODE = 2;
    private Bundle mBundle = new Bundle();
    private HelperModel mHelper;
    private boolean mPermissions = false;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.txt_empty_content)
    TextView mEmptyContentText;

    @BindView(R.id.content_main)
    FrameLayout mContent;

    @BindView(R.id.drawer)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mHelper = new HelperModelImpl();

        mBundle.putInt("spectrum", 0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        requestMultiplePermissions();
    }

    private void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                FILE_SELECT_CODE);
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mContent, getString(R.string.err_permissions_denied), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.err_button_perm_den), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestMultiplePermissions();
                        }
                    })
                    .show();
        } else mPermissions = true;
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
                checkForPermission();
                if (mPermissions) showFileChooser(FILE_SELECT_CODE);
                break;

            case R.id.action_load_all:
                checkForPermission();
                if (mPermissions) showFileChooser(ARCHIVE_SELECT_CODE);
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
                intent.setType("application/*");
                break;
        }

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.err_file_for_download)),
                    selectCode);
        } catch (ActivityNotFoundException ex) {

            showToast(getString(R.string.err_file_manager));
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
                    showToast(path);
                    Intent intent = new Intent(this, ResultTabbedActivity.class);
                    mBundle.putString("fileListPath", path);
                    mBundle.putInt("itemPos", -1);
                    mBundle.putBoolean("isMenuEnabled", false);

                    intent.putExtras(mBundle);
                    startActivity(intent);

                }
                break;

            case ARCHIVE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(this, uri);
                    showToast(path);

                    mBundle.putString("fileListPath", path);
                    setFragment();
                }
                break;

            case RESULT_CANCELED:
                break;
        }
    }

    private static String getPath(Context context, Uri uri) {
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


    private void setFragment() {
        Fragment fragment = new WavChooseFragment();

        mEmptyContentText.setVisibility(View.INVISIBLE);

        mHelper.swapFragment(getSupportFragmentManager(), fragment, mBundle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_load_wav_nav:
                checkForPermission();
                if (mPermissions) showFileChooser(FILE_SELECT_CODE);
                break;

            case R.id.action_load_all_nav:
                checkForPermission();
                if (mPermissions) showFileChooser(ARCHIVE_SELECT_CODE);
                break;

            case R.id.action_load_one_example:
                openWebURL(getString(R.string.example_wav_url));
                break;

            case R.id.action_load_zip_example:
                openWebURL(getString(R.string.example_zip_url));
                break;

            case R.id.action_to_store:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.gooday.talbotgooday.rec"));
                startActivity(intent);
                break;

            case R.id.action_to_github:
                openWebURL(getString(R.string.github_url));
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

        startActivity(browse);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}

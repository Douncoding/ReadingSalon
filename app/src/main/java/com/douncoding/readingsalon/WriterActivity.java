package com.douncoding.readingsalon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class WriterActivity extends AppCompatActivity implements WriteFragment.OnListener {
    public static final String TAG = WriterActivity.class.getSimpleName();
    public static final String EXTRA_PARAMS = "param.contents.json";
    final int REQ_CODE_SELECT_IMAGE = 100;

    String mContentsJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mContentsJson = getIntent().getStringExtra(EXTRA_PARAMS);
        showWriterView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showWriterView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.writer_container, WriteFragment.newInstance(mContentsJson),
                        WriteFragment.TAG)
                .commit();
    }

    @Override
    public void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(Intent.ACTION_PICK);
        takePhotoIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        takePhotoIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePhotoIntent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, data.getDataString());
        if (requestCode == REQ_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().
                    findFragmentByTag(WriteFragment.TAG);

            if (fragment != null) {
                ((WriteFragment)fragment).setPhoto(data.getData());
            }
        }
    }
}

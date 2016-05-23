package com.douncoding.readingsalon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class WriterActivity extends AppCompatActivity {
    public static final String TAG = WriterActivity.class.getSimpleName();
    public static final String EXTRA_PARAMS = "param.contents.json";

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContentsJson = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWriterView();
    }

    private void showWriterView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.writer_container, WriteFragment.newInstance(mContentsJson))
                .commit();
    }
}

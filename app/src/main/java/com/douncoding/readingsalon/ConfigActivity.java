package com.douncoding.readingsalon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ConfigActivity extends AppCompatActivity {
    public static final String TAG = ConfigActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showConfigMenuFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfigMenuFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.config_container, ConfigMenuFragment.newInstance())
                .commitAllowingStateLoss();
    }

}

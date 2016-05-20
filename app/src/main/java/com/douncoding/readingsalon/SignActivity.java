package com.douncoding.readingsalon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSignInView();
    }

    private void showSignInView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.sign_view, SignInFragment.newInstance())
                .commitAllowingStateLoss();
    }


}

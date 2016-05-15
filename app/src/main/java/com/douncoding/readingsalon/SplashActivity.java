package com.douncoding.readingsalon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = SplashActivity.class.getSimpleName();

    GcmBuilder mGcmBuilder;
    InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mGcmBuilder = new GcmBuilder(getApplicationContext());
        mGcmBuilder.setOnListener(new GcmBuilder.OnListener() {
            @Override
            public void onSetup() {
                showAdMod();
            }

            @Override
            public void onFailure(String where) {
                Log.e(TAG, "서버상태를 확인할 필요가 있음: 예외처리 없는 상태");
                Toast.makeText(SplashActivity.this
                        , "네트워크 상태를 확인하고 다시 시도하세요."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGcmBuilder.build();
    }

    private void showAdMod() {
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(Constants.ADMOD_KEY);

        AdRequest adRequest = new AdRequest.Builder().build();

        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitial.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}

package com.douncoding.readingsalon.controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.douncoding.readingsalon.Constants;
import com.douncoding.readingsalon.GcmBuilder;
import com.douncoding.readingsalon.WriterActivity;
import com.douncoding.readingsalon.data.Owner;

public class ConfigInteractor {
    public static final String TAG = ConfigInteractor.class.getSimpleName();

    Context mContext;
    Owner mOwner;
    GcmBuilder mGcmBuilder;
    PackageInfo mPackageInfo;

    public ConfigInteractor(Context context) {
        mGcmBuilder = new GcmBuilder(context);
        mContext = context;
        mOwner = new Owner(context);

        try {
            mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException e) { }
    }

    public boolean isPushService() {
        return mGcmBuilder.isPushServiceOn();
    }

    public void changePushService(boolean state) {
        mGcmBuilder.changePushState(state, new GcmBuilder.OnPushListener() {
            @Override
            public void onChanged() {
                Log.d(TAG, "푸쉬서비스 상태 변경완료");
            }
        });
    }

    public void logout() {
        mOwner.delete();
        Log.i(TAG, "로그아웃 실행: SharedPreferences 초기화");
    }

    public String getAppVersion() {
        return "현재: " + mPackageInfo.versionName;
    }
}

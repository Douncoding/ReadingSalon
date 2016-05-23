package com.douncoding.readingsalon;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.douncoding.readingsalon.data.Owner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContext extends Application {
    public static final String TAG = AppContext.class.getSimpleName();

    Owner mOwner;

    @Override
    public void onCreate() {
        super.onCreate();

        mOwner = new Owner(this);
    }

    public Retrofit getWebResource() {
        return new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

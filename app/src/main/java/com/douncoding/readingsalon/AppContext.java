package com.douncoding.readingsalon;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.douncoding.readingsalon.dao.DaoMaster;
import com.douncoding.readingsalon.dao.DaoSession;
import com.douncoding.readingsalon.data.Owner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContext extends Application {
    public static final String TAG = AppContext.class.getSimpleName();

    DaoMaster.DevOpenHelper mHelper;

    Owner mOwner;

    @Override
    public void onCreate() {
        super.onCreate();

        mHelper = new DaoMaster.DevOpenHelper(this, Constants.DATABASE_NAME, null);

        mOwner = new Owner(this);
    }

    public Retrofit getWebResource() {
        return new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public DaoSession openDBReadable() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        return daoMaster.newSession();
    }

    public DaoSession openDBWritable() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        return daoMaster.newSession();
    }

}

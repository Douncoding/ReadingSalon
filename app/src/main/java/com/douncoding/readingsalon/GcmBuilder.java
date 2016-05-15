package com.douncoding.readingsalon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 구글 GCM 서비스 사용을 위한 모든 처리를 담당
 *
 * 1. 서버의 등록된 키값 얻기
 * 2. 내부의 저장된 키값 얻기
 * 3-1. 내부 저장된 키값이 없는 경우 구글서버 등록 및 요청
 * 3-2. 내부와 서버의 키값이 동일한 경우 완료
 * 4. 서버 키와 내부 키 동기화
 * 5. 완료
 */
public class GcmBuilder {
    public static String TAG = GcmBuilder.class.getSimpleName();

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String SENDER_ID = "865257072381";

    GoogleCloudMessaging mGcm;
    AppContext mApp;
    SharedPreferences mPreferences;
    GcmWebService mWebService;

    OnListener onListener;

    String mAndroidKey;

    public GcmBuilder(Context context) {
        mGcm = GoogleCloudMessaging.getInstance(context);
        mPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        mAndroidKey = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);

        mApp = (AppContext)context;
        mWebService = mApp.getWebResource().create(GcmWebService.class);

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)
                != ConnectionResult.SUCCESS) {
            throw new RuntimeException("No valid Google Play Services APK found.");
        }
    }

    public void build() {
        new AsyncTask<Void, Void, Void>() {
            String localKey;
            String serverKey;

            @Override
            protected Void doInBackground(Void... params) {
                localKey = loadLocalRegistrationKey();
                serverKey = loadServerRegistrationKey();

                if (localKey.isEmpty()) {
                    Log.d(TAG, "내부키 없음");
                    publishGoogleServerKey();
                } else {
                    Log.d(TAG, String.format("내부키:%s 서버키:%s", localKey, serverKey));
                    if (!localKey.equals(serverKey)) {
                        Log.w(TAG, "내부키와 서버키 동기화 필요: 재발급");
                        publishGoogleServerKey();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (onListener != null)
                    onListener.onSetup();
            }
        }.execute();
    }

    public void setOnListener(OnListener listener) {
        this.onListener = listener;
    }

    public interface OnListener {
        void onSetup(); // 정상 설정 완료 시 호출
        void onFailure(String where); // 동기화 중 실패 시 호출
    }

    private interface GcmWebService {
        @GET("/push/{androidKey}")
        Call<KeyUnit> getServerKey(@Path("androidKey") String androidKey);

        @PUT("/push/{androidKey}")
        Call<Void> putServerKey(@Path("androidKey") String androidKey, @Body KeyUnit unit);
    }

    class KeyUnit {
        int id;
        String deviceId;
        String gcm;
        int allow;
    }

    private String loadServerRegistrationKey() {
        try {
            KeyUnit keyUnit = mWebService.getServerKey(mAndroidKey).execute().body();

            if (keyUnit == null) {
                Log.w(TAG, "서버 GCM KEY 얻기:없음");
                return "";
            } else {
                Log.i(TAG, "서버 GCM KEY 얻기:" + keyUnit.gcm);
                return keyUnit.gcm;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (onListener != null)
                onListener.onFailure("loadServerRegistrationKey");
        }

        return "";
    }

    private void storeServerRegistrationKey(String regKey) {
        KeyUnit keyUnit = new KeyUnit();
        keyUnit.deviceId = mAndroidKey;
        keyUnit.gcm = regKey;

        try {
            mWebService.putServerKey(mAndroidKey, keyUnit).execute().body();
            Log.i(TAG, "서버 GCM KEY 등록");
        } catch (IOException e) {
            e.printStackTrace();
            if (onListener != null)
                onListener.onFailure("storeServerRegistrationKey");
        }
    }

    private String loadLocalRegistrationKey() {
        String regKey = mPreferences.getString(PROPERTY_REG_ID, "");
        if (regKey.isEmpty()) {
            Log.w(TAG, "GCM KEY 를 찾을 수 없음");
            return "";
        }

        Log.i(TAG, "내부 GCM KEY 얻기:" + regKey);
        return regKey;
    }

    private void storeLocalRegistrationKey(String regKey) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regKey);
        editor.apply();
        Log.i(TAG, "내부 GCM KEY 저장:" + regKey);
    }

    private void publishGoogleServerKey() {
        String key;

        try {
            key = mGcm.register(SENDER_ID);

            storeLocalRegistrationKey(key);
            storeServerRegistrationKey(key);
        } catch (IOException e) {
            e.printStackTrace();
            if (onListener != null)
                onListener.onFailure("publishGoogleServerKey");
        }
    }
}

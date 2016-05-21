package com.douncoding.readingsalon.controller;

import android.content.Context;
import android.util.Log;

import com.douncoding.readingsalon.AppContext;
import com.douncoding.readingsalon.dao.Member;
import com.douncoding.readingsalon.data.Owner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class SignInteractor {
    public static final String TAG = SignInteractor.class.getSimpleName();

    WebService mWebService;
    Owner owner;

    private interface WebService {
        @GET("/members")
        Call<Member> read(@Query("email") String email, @Query("password") String password);

        @POST("/members")
        Call<Member> create(@Body Member member);

        @DELETE("/members")
        Call<Member> delete(@Query("email") String email, @Query("password") String password);

        @PUT("/members")
        Call<Member> update(@Body Member member);
    }

    public SignInteractor(Context context) {
        AppContext app = (AppContext)context.getApplicationContext();

        mWebService = app.getWebResource().create(WebService.class);
        owner = new Owner(context);
    }

    public interface OnCallback {
        void onCallback(Object obj);
    }

    public void login(String email, String password, final OnCallback listener) {
        mWebService.read(email, password).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member == null) {
                    Log.w(TAG, "로그인 실패: 응답결과 없음");
                    return;
                }

                owner.store(member);
                if (listener != null) {
                    listener.onCallback(true);
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                t.printStackTrace();
                if (listener != null) {
                    listener.onCallback(false);
                }
            }
        });
    }

    public void logout() {
        owner.delete();
    }

    public void signup() {

    }

    public void signdown() {

    }
}

package com.douncoding.readingsalon;

import android.content.Context;
import android.util.Log;

import com.douncoding.readingsalon.data.Contents;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 웹 서버와 통신하며
 */
public class ContentsInteractor {
    public static final String TAG = ContentsInteractor.class.getSimpleName();

    public static final int DEFAULT_LOAD_COUNT = 20;

    Context context;
    AppContext mApp;
    WebService mWebService;

    OnListener onListener;
    OnFavorListener onFavorListener;

    public ContentsInteractor(Context context) {
        this.context = context;
        mApp = (AppContext)context.getApplicationContext();
        mWebService = mApp.getWebResource().create(WebService.class);
    }

    private interface WebService {
        @GET("/contents")
        Call<List<Contents>> readContentsList(
                @Query("type") int type, @Query("offset") int offset, @Query("limit") int limit);

        @GET("/contents/{id}")
        Call<Contents> readContents(@Path("id") int contentsId);

        @GET("/favorites/members/{userId}")
        Call<List<Contents>> getFavorites(
                @Path("userId") int userId, @Query("offset") int offset, @Query("limit") int limit);

        @POST("/favorites/members/{mid}/contents/{cid}")
        Call<Void> createFavorites(@Path("mid") int mid, @Path("cid") int cid);

        @DELETE("/favorites/members/{mid}/contents/{cid}")
        Call<Void> deleteFavorites(@Path("mid") int mid, @Path("cid") int cid);
    }

    public interface OnListener {
        void onLoad(ContentsType type, List<Contents> items);
    }

    public void setOnListener(OnListener listener) {
        this.onListener = listener;
    }

    public interface OnDetailListener {
        void onLoad(Contents contents);
    }

    public interface OnFavorListener {
        void onLike();
        void onUnLike();
    }

    public void setOnFavorListener(OnFavorListener listener) {
        this.onFavorListener = listener;
    }


    public void load(final ContentsType type, int offset, int limit) {
        Call<List<Contents>> call;
        if (ContentsType.FAVOR.equals(type)) {
            call = mWebService.getFavorites(mApp.mOwner.getId(), offset, limit);
        } else {
            call = mWebService.readContentsList(type.getValue(), offset, limit);
        }

        call.enqueue(new Callback<List<Contents>>() {
            @Override
            public void onResponse(Call<List<Contents>> call, Response<List<Contents>> response) {
                List<Contents> contentsList = response.body();
                if (onListener != null)
                    onListener.onLoad(type, contentsList);
            }

            @Override
            public void onFailure(Call<List<Contents>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getContens(int contentsId, final OnDetailListener listener) {
        mWebService.readContents(contentsId).enqueue(new Callback<Contents>() {
            @Override
            public void onResponse(Call<Contents> call, Response<Contents> response) {
                Log.d(TAG, "세부정보 조회 성공:" + new Gson().toJson(response.body()));

                if (listener != null)
                    listener.onLoad(response.body());
            }

            @Override
            public void onFailure(Call<Contents> call, Throwable t) {
                Log.e(TAG, "세부정보 조회 실패:");
                t.printStackTrace();
            }
        });
    }

    public void like(int userId, int contentsId) {
        mWebService.createFavorites(userId, contentsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "모아보기 등록 결과: " + response.code());

                if (onFavorListener != null)
                    onFavorListener.onLike();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void unlike(int userId, int contentsId) {
        mWebService.deleteFavorites(userId, contentsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "모아보기 삭제 결과: " + response.code());

                if (onFavorListener != null)
                    onFavorListener.onUnLike();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}

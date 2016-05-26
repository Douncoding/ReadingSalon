package com.douncoding.readingsalon.controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.douncoding.readingsalon.AppContext;
import com.douncoding.readingsalon.ContentsType;
import com.douncoding.readingsalon.Utils;
import com.douncoding.readingsalon.data.Contents;
import com.douncoding.readingsalon.data.Owner;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    public ContentsInteractor(Context context) {
        this.context = context;
        mApp = (AppContext)context.getApplicationContext();
        mWebService = mApp.getWebResource().create(WebService.class);
    }

    class PushData {
        String message;
        int cid;
    }

    private interface WebService {
        @GET("/contents")
        Call<List<Contents>> readContentsList(
                @Query("type") int type, @Query("offset") int offset, @Query("limit") int limit);

        @GET("/contents/{id}")
        Call<Contents> readContents(@Path("id") int contentsId);

        @DELETE("/contents/{id}")
        Call<Void> delete(@Path("id") int contentsId);

        @Multipart
        @POST("/contents")
        Call<Contents> write(@Part() List<MultipartBody.Part> body);

        @Multipart
        @PUT("/contents/{id}")
        Call<Contents> modify(@Path("id") int contentsId, @Part() List<MultipartBody.Part> body);

        @POST("/push/send")
        Call<Void> push(@Body PushData data);

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

    public interface OnCommonListener {
        void onReceived(Object obj);
    }

    public void load(final ContentsType type, int offset, int limit) {
        Call<List<Contents>> call;
        if (ContentsType.FAVOR.equals(type)) {
            call = mWebService.getFavorites(new Owner(context).getId(), offset, limit);
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

    public void upload(Contents contents, final OnCommonListener listener) {
        String path = Utils.getAbsolutePathFromUri(context, Uri.parse(contents.getImage()));

        Log.d(TAG, "포스트 작성 요청:" + new Gson().toJson(contents));
        Log.d(TAG, "포스트 사진 경로:" + path);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("type", String.valueOf(contents.getType()))
                .addFormDataPart("title", contents.getTitle())
                .addFormDataPart("content", contents.getContent())
                .addFormDataPart("subject", contents.getSubject())
                .addFormDataPart("overview", contents.getOverview());

        if (path != null) {
            File file = new File(path);
            builder.addFormDataPart("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));
        }

        List<MultipartBody.Part> parts = builder.build().parts();
        mWebService.write(parts).enqueue(new Callback<Contents>() {
            @Override
            public void onResponse(Call<Contents> call, Response<Contents> response) {
                listener.onReceived(response.body());
            }

            @Override
            public void onFailure(Call<Contents> call, Throwable t) {
                t.printStackTrace();
                listener.onReceived(false);
            }
        });
    }

    public void update(Contents contents, final OnCommonListener listener) {
        String path = Utils.getAbsolutePathFromUri(context, Uri.parse(contents.getImage()));

        Log.d(TAG, "포스트 수정 요청:" + new Gson().toJson(contents));
        Log.d(TAG, "포스트 사진 경로:" + path);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("type", String.valueOf(contents.getType()))
                .addFormDataPart("title", contents.getTitle())
                .addFormDataPart("content", contents.getContent())
                .addFormDataPart("subject", contents.getSubject())
                .addFormDataPart("overview", contents.getOverview());

        if (path != null) {
            File file = new File(path);
            builder.addFormDataPart("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));
        }

        List<MultipartBody.Part> parts = builder.build().parts();
        mWebService.modify(contents.getId(), parts).enqueue(new Callback<Contents>() {
            @Override
            public void onResponse(Call<Contents> call, Response<Contents> response) {
                Log.d(TAG, "포스트 수정 완료:");
                listener.onReceived(true);
            }

            @Override
            public void onFailure(Call<Contents> call, Throwable t) {
                Log.d(TAG, "포스트 수정 실패:");
                t.printStackTrace();
                listener.onReceived(false);
            }
        });
    }

    public void remove(int contentsId, final OnCommonListener listener) {
        if (contentsId <= 0) {
            Log.w(TAG,"포스트 삭제 요청 실패: 잘못된 매개변수: " + contentsId);
            listener.onReceived(null);
            return;
        }

        mWebService.delete(contentsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Log.i(TAG, "포스트 삭제 요청 성공:");
                    listener.onReceived(true);
                } else {
                    Log.w(TAG, "포스트 삭제 요청 실패:" + response.code());
                    listener.onReceived(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.w(TAG, "포스트 삭제 요청 실패: 서버 요류:" + t.toString());
                listener.onReceived(false);
            }
        });
    }

    public void like(int contentsId, final OnCommonListener listener) {
        int userId = new Owner(context).getId();
        mWebService.createFavorites(userId, contentsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "모아보기 등록 성공: " + response.code());
                listener.onReceived(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "모아보기 등록 실패: ");
                t.printStackTrace();
                listener.onReceived(false);
            }
        });
    }

    public void unlike(int contentsId, final OnCommonListener listener) {
        int userId = new Owner(context).getId();
        mWebService.deleteFavorites(userId, contentsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "모아보기 삭제 성공: " + response.code());
                listener.onReceived(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "모아보기 삭제 실패: ");
                t.printStackTrace();
                listener.onReceived(false);
            }
        });
    }

    public void pushMessage(String message, int contentsId) {
        if (message == null || contentsId <= 0) {
            Log.w(TAG, "푸쉬 전송: 잘못된 매개변수: ");
            return;
        }

        PushData data = new PushData();
        data.message = message;
        data.cid = contentsId;

        mWebService.push(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "푸쉬 전송 성공:" + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "푸쉬 전송 실패: " + t.toString());
            }
        });
    }
}

package com.douncoding.readingsalon.controller;

import android.content.Context;
import android.util.Log;

import com.douncoding.readingsalon.AppContext;
import com.douncoding.readingsalon.data.Comment;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class CommentInteractor {
    public static final String TAG = CommentInteractor.class.getSimpleName();

    AppContext mApp;
    WebService mWebService;

    OnListener onListener;

    private interface WebService {
        @GET("/comments")
        Call<List<Comment>> read(@Query("contentsId") int contentsId);

        @POST("/comments")
        Call<Comment> create(@Body Comment comment);

        @DELETE("/comments/{id}")
        Call<Comment> delete(@Path("id") int commentId);

        @PUT("/comments/{id}")
        Call<Comment> update(@Path("id") int commentId, @Body Comment comment);
    }

    public CommentInteractor(Context context) {
        mApp = (AppContext)context.getApplicationContext();

        mWebService = mApp.getWebResource().create(WebService.class);
    }

    public interface OnListener {
        void onReaded(List<Comment> comments);
        void onCreated(Comment comment);
        void onDeleted(Comment comment);
        void onUpdated(Comment comment);
        void onError();
    }

    public void setOnListener(OnListener listener) {
        this.onListener = listener;
    }

    public void read(int id) {
        mWebService.read(id).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                Log.d(TAG, "댓글 읽기 성공: " + response.code());

                if (onListener != null)
                    onListener.onReaded(response.body());
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.w(TAG, "댓글 읽기 실패: ");
                t.printStackTrace();

                if (onListener != null)
                    onListener.onError();
            }
        });
    }

    public void post(Comment comment) {
        mWebService.create(comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.d(TAG, "댓글 쓰기 성공: " + response.code());

                if (onListener != null)
                    onListener.onCreated(response.body());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.w(TAG, "댓글 쓰기 실패: ");
                t.printStackTrace();

                if (onListener != null)
                    onListener.onError();
            }
        });
    }

    public void remove(final Comment comment) {
        mWebService.delete(comment.getId()).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.d(TAG, "댓글 삭제 성공: " + new Gson().toJson(comment));
                if (onListener != null)
                    onListener.onDeleted(comment);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d(TAG, "댓글 삭제 실패:");
                t.printStackTrace();
                if (onListener != null)
                    onListener.onError();
            }
        });
    }
}

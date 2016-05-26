package com.douncoding.readingsalon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.util.Util;
import com.douncoding.readingsalon.data.Contents;
import com.enrique.stackblur.StackBlurManager;
import com.google.gson.Gson;

public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    public static String getServerImage(String image) {
        return Constants.HOST + "/image/" + image;
    }

    public static boolean isLocalMedia(String image) {
        String[] element = image.split(":");
        return  element[0].equals("content");
    }

    /*
    @Deprecated
    public static void navigateToDetailActivity(Context context, Contents contents) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("contents", new Gson().toJson(contents));
        context.startActivity(intent);
    }
    */

    public static void navigateToDetailActivity(Context context, int contentsId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("contents", contentsId);
        context.startActivity(intent);
    }

    public static void navigateToSignActivity(Context context) {
        Intent loginIntent = new Intent(context, SignActivity.class);
        context.startActivity(loginIntent);
    }

    public static void navigateToWriterActivity(Context context, Contents contents) {
        Intent intent = new Intent(context, WriterActivity.class);

        if (contents != null)
            intent.putExtra(WriterActivity.EXTRA_PARAMS, new Gson().toJson(contents));

        context.startActivity(intent);
    }

    public static void showNeedsLoginServiceDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title("사용 제한")
                .content("로그인이 필요한 서비스입니다.")
                .positiveText(android.R.string.ok)
                .show();
    }

    public static Bitmap buildBlurImage(ImageView view, Bitmap bmOrg) {
        Bitmap bmBlur = new StackBlurManager(bmOrg).process(50);
        Bitmap bmResize = Bitmap.createScaledBitmap(bmOrg, bmOrg.getWidth() / 2, bmOrg.getHeight() / 2, false);

        final Bitmap bmOverlay = Bitmap.createBitmap(bmBlur.getWidth(), bmBlur.getHeight(), bmBlur.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(bmBlur, new Matrix(), null);

        int left = (bmBlur.getWidth() / 2) - (bmResize.getWidth() / 2);
        int top = (bmBlur.getHeight() / 2) - (bmResize.getHeight() / 2);

        canvas.drawBitmap(bmResize, left, top, null);

        return bmOverlay;
    }

    public static Bitmap buildBlindImage(ImageView view, Bitmap bmOrg) {
        // 블라인드 이미지 생성
        Bitmap bmBlind = Bitmap.createBitmap(bmOrg.getWidth(), bmOrg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bCanvas = new Canvas(bmBlind);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(192);
        bCanvas.drawRect(0, 0, bCanvas.getWidth(), bCanvas.getHeight(), paint);

        // 블라인드 효과 적용
        Bitmap bmOverlay = Bitmap.createBitmap(bmBlind.getWidth(), bmBlind.getHeight(), bmBlind.getConfig());
        Canvas oCanvas = new Canvas(bmOverlay);
        oCanvas.drawBitmap(bmOrg, new Matrix(), null);
        oCanvas.drawBitmap(bmBlind, new Matrix(), null);
        return bmOverlay;
    }


    public static String getAbsolutePathFromUri(Context ctx, Uri uri) {
        Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        } else {
            Log.w(TAG, "Cursor is null");
            return null;
        }
    }
}

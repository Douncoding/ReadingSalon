package com.douncoding.readingsalon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.douncoding.readingsalon.controller.ContentsInteractor;
import com.douncoding.readingsalon.data.Contents;
import com.douncoding.readingsalon.data.Owner;
import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = DetailActivity.class.getSimpleName();

    ImageView mPostEdit;

    Contents mContents;
    ContentsInteractor mInteractor;

    OnKeyBackPressedListener onKeyBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mPostEdit = (ImageView)findViewById(R.id.post_edit);
        mPostEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateToWriterActivity(DetailActivity.this, mContents);
            }
        });

        mInteractor = new ContentsInteractor(this);

        int contentsId = getIntent().getIntExtra("contents", 0);
        if (contentsId <= 0) {
            Log.w(TAG, "세부화면 실행 실패: 잘못된 매개변수 ID:" + contentsId);
            finish();
        } else {
            mContents = new Contents();
            mContents.setId(contentsId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContents = null;
        Log.w(TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (new Owner(this).isWriter()) {
            mPostEdit.setVisibility(View.VISIBLE);
        } else {
            mPostEdit.setVisibility(View.GONE);
        }

        mInteractor.getContens(mContents.getId(), new ContentsInteractor.OnDetailListener() {
            @Override
            public void onLoad(Contents contents) {
                mContents = contents;
                showPostFragment();
            }
        });
    }

    public interface OnKeyBackPressedListener {
        void onBack();
    }

    public void showPostFragment() {
       getSupportFragmentManager().beginTransaction()
               .replace(R.id.detail_container,
                        PostFragment.newInstance(new Gson().toJson(mContents)))
               .commit();

        onKeyBackPressedListener = null;
    }

    public void showCommentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container,
                        CommentsFragment.newInstance(mContents.getId()))
                .commit();

        onKeyBackPressedListener = new OnKeyBackPressedListener() {
            @Override
            public void onBack() {
                showPostFragment();
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (onKeyBackPressedListener != null) {
            onKeyBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }
}

package com.douncoding.readingsalon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.douncoding.readingsalon.controller.ContentsInteractor;
import com.douncoding.readingsalon.data.Contents;
import com.douncoding.readingsalon.data.Owner;
import com.google.gson.Gson;

public class PostFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = PostFragment.class.getSimpleName();
    public static final String EXTRA_PARAMS = "post";

    // 인터페이스
    TextView mCreatedDate;
    TextView mUpdatedDate;
    TextView mContentsText;
    TextView mTitleText;
    ImageView mImageView;
    LinearLayout mCommentCountView;
    TextView mCommentCount;
    LinearLayout mSharedCountView;
    TextView mSharedCount;

    // 리소스
    AppContext mApp;
    ContentsInteractor mInteractor;
    Owner mOwner;

    // 참조
    Contents mContents;

    public static PostFragment newInstance(String jsonPost) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PARAMS, jsonPost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String json = getArguments().getString(EXTRA_PARAMS);
        if (json != null) {
            Log.d(TAG, "포스트 정보:" + json);
            mContents = new Gson().fromJson(json, Contents.class);
        } else {
            Log.w(TAG, "포스트 찾을 수 없음:");
            getActivity().finish();
        }

        mOwner = new Owner(getContext());
        mApp = (AppContext)getContext().getApplicationContext();
        mInteractor = new ContentsInteractor(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mCreatedDate = (TextView)view.findViewById(R.id.post_created_date);
        mUpdatedDate = (TextView)view.findViewById(R.id.post_updated_date);
        mContentsText = (TextView)view.findViewById(R.id.detail_content);
        mTitleText = (TextView)view.findViewById(R.id.detail_title);
        mImageView = (ImageView)view.findViewById(R.id.detail_image);

        mCommentCount = (TextView)view.findViewById(R.id.comment_count);
        mCommentCountView = (LinearLayout)view.findViewById(R.id.comment_count_view);
        mCommentCountView.setOnClickListener(this);

        mSharedCount = (TextView)view.findViewById(R.id.shared_count);
        mSharedCountView = (LinearLayout)view.findViewById(R.id.shared_count_view);
        mSharedCountView.setOnClickListener(this);

        if (mContents != null) {
            Glide.with(getContext())
                    .load(Utils.getServerImage(mContents.getImage()))
                    .placeholder(R.drawable.image_loading_animate)
                    .into(mImageView);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePostStatus();
    }


    /**
     * 제목, 내용, 이미지는 최소 뷰 생성시 한번만 로딩하지만, 댓글 및 담기 수는 새로고침 시점마다
     * 다시 읽어 현시한다.
     */
    private void updatePostStatus() {
        mInteractor.getContens(mContents.getId(), new ContentsInteractor.OnDetailListener() {
            @Override
            public void onLoad(Contents contents) {
                if (contents == null) {
                    Log.w(TAG, "포스트 정보를 읽을 수 없음: 서버 정보 없음");
                    getActivity().finish();
                } else {
                    mContents = contents;

                    mCreatedDate.setText(mContents.getCreatedAt());
                    mUpdatedDate.setText(mContents.getUpdatedAt());
                    mTitleText.setText(mContents.getTitle());
                    mContentsText.setText(mContents.getContent());
                    mCommentCount.setText(String.valueOf(mContents.getCommentCount()));
                    mSharedCount.setText(String.valueOf(mContents.getFavoritesCount()));
                }
            }
        });
    }

    private void showFavoritesDeleteDialog() {
        new MaterialDialog.Builder(getContext())
                .title("모아보기 삭제")
                .content("이미 등록된 모아보기 입니다. 모아보기를 삭제 하시겠습니까?")
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mInteractor.unlike(mContents.getId(), new ContentsInteractor.OnCommonListener() {
                            @Override
                            public void onReceived(Object obj) {
                                Toast.makeText(getContext()
                                        , "모아보기 삭제"
                                        , Toast.LENGTH_SHORT).show();
                                updatePostStatus();
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_count_view:
                ((DetailActivity)getActivity()).showCommentFragment();
                break;
            case R.id.shared_count_view:
                if (mOwner.isLogin()) {
                    mInteractor.like(mContents.getId(), new ContentsInteractor.OnCommonListener() {
                        @Override
                        public void onReceived(Object obj) {
                            Integer code = (Integer) obj;
                            switch (code) {
                                case 200:
                                    Toast.makeText(getContext()
                                            , "모아보기 등록"
                                            , Toast.LENGTH_SHORT).show();
                                    updatePostStatus();
                                    break;
                                case 400:
                                    showFavoritesDeleteDialog();
                                    break;
                            }
                        }
                    });
                } else {
                    Utils.showNeedsLoginServiceDialog(getContext());
                }

                break;
        }
    }
}

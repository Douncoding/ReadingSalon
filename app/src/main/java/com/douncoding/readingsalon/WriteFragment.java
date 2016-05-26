package com.douncoding.readingsalon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.douncoding.readingsalon.controller.ContentsInteractor;
import com.douncoding.readingsalon.data.Contents;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WriteFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = WriteFragment.class.getSimpleName();

    public static final String EXTRA_PARAMS1 = "param1";

    EditText mTitleText;
    EditText mContentText;
    EditText mSubjectText;
    EditText mOverviewText;
    EditText mPushText;
    Button mSelectImage;
    Button mPostIn, mPostOut;

    RecyclerView mPreviewRecyclerView;
    Spinner mSpinner;
    ArrayAdapter<String> mSpinnerAdapter;
    View mExpandView;

    Contents mContents;
    ContentsAdapter mPreviewAdapter;
    ContentsInteractor mInteractor;

    public static WriteFragment newInstance(String param) {
        WriteFragment fragment = new WriteFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PARAMS1, param);
        fragment.setArguments(args);
        return fragment;
    }

    private OnListener listener;
    public interface OnListener {
        void dispatchTakePhotoIntent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must be implements OnListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreviewAdapter = new ContentsAdapter();
        mSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.planets_array));

        String json = getArguments().getString(EXTRA_PARAMS1);
        if (json != null) {
            Log.d(TAG, "포스트 수정:" + json);
            mContents = new Gson().fromJson(json, Contents.class);
        } else {
            Log.d(TAG, "포스트 생성:");
            mContents = new Contents();
        }

        mInteractor = new ContentsInteractor(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSpinnerAdapter = null;
        mPreviewAdapter = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_writer, container, false);

        mExpandView = rootView.findViewById(R.id.expand_container);
        mExpandView.setEnabled(false);

        mSpinner = (Spinner)rootView.findViewById(R.id.select_cardtype);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mContents.setType(position);
                clearInputForm();
                setInputForm();

                if (position == (mSpinnerAdapter.getCount() - 1))
                    mExpandView.setVisibility(View.VISIBLE);
                else
                    mExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mTitleText = (EditText)rootView.findViewById(R.id.form_title);
        mContentText = (EditText)rootView.findViewById(R.id.form_content);
        mSubjectText = (EditText)rootView.findViewById(R.id.form_subject);
        mOverviewText = (EditText)rootView.findViewById(R.id.form_overview);
        mPushText = (EditText)rootView.findViewById(R.id.form_push);

        mPreviewRecyclerView = (RecyclerView)rootView.findViewById(R.id.form_preview);
        mPreviewRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mPreviewRecyclerView.setAdapter(mPreviewAdapter);

        mPostIn = (Button)rootView.findViewById(R.id.post_in);
        mPostIn.setOnClickListener(this);
        mPostOut = (Button)rootView.findViewById(R.id.post_out);
        mPostOut.setOnClickListener(this);
        mSelectImage = (Button)rootView.findViewById(R.id.form_select_image);
        mSelectImage.setOnClickListener(this);

        if (mContents.getId() <= 0) {
            mPostIn.setText("작성");
            mPostOut.setVisibility(View.GONE);
        } else {
            mPostIn.setText("수정");
            mPostOut.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    public void setPhoto(Uri uri) {
        mContents.setTitle(mTitleText.getText().toString());
        mContents.setContent(mContentText.getText().toString());
        mContents.setSubject(mSubjectText.getText().toString());
        mContents.setOverview(mOverviewText.getText().toString());
        mContents.setImage(uri.toString());

        setInputForm();

        List<Contents> items = new ArrayList<>();
        items.add(mContents);
        items.add(mContents);

        mPreviewAdapter.clear();
        mPreviewAdapter.add(items);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_in:
                postIn();
                break;
            case R.id.post_out:
                postOut();
                break;
            case R.id.form_select_image:
                listener.dispatchTakePhotoIntent();
                break;
        }
    }

    private void clearInputForm() {
        Log.d(TAG, "포스트 작성 폼: 초기화");

        mTitleText.setText("");
        mContentText.setText("");
        mOverviewText.setText("");
        mPushText.setText("");
        mSubjectText.setText("");
        mContentText.setText("");
        mPreviewAdapter.clear();
    }

    private void setInputForm() {
        Log.d(TAG, "포스트 작성 폼: 입력:" + new Gson().toJson(mContents));
        if (mContents != null) {
            mSpinner.setSelection(mContents.getType());
            mTitleText.setText(mContents.getTitle());
            mContentText.setText(mContents.getContent());

            if (mContents.getSubject() != null)
                mSubjectText.setText(mContents.getSubject());

            if (mContents.getOverview() != null)
                mOverviewText.setText(mContents.getOverview());
        }
    }

    private void postIn() {
        mContents.setTitle(mTitleText.getText().toString());
        mContents.setContent(mContentText.getText().toString());
        mContents.setSubject(mSubjectText.getText().toString());
        mContents.setOverview(mOverviewText.getText().toString());

        if (TextUtils.isEmpty(mContents.getTitle()) ||
            TextUtils.isEmpty(mContents.getContent())) {
            Toast.makeText(getActivity()
                    , "제목과 내용은 꼭 입력해야 합니다. 다시 확인하세요."
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if (mContents.getId() <= 0) {
            mInteractor.upload(mContents, new ContentsInteractor.OnCommonListener() {
                @Override
                public void onReceived(Object obj) {
                    Contents contents = (Contents)obj;
                    Toast.makeText(getContext()
                            , contents.getTitle() + "포스트 작성 완료"
                            , Toast.LENGTH_SHORT).show();

                    mInteractor.pushMessage(mPushText.getText().toString(), contents.getId());
                    getActivity().finish();
                }
            });
        } else {
            mInteractor.update(mContents, new ContentsInteractor.OnCommonListener() {
                @Override
                public void onReceived(Object obj) {
                    if ((boolean)obj) {
                        Toast.makeText(getContext()
                                , "포스트 수정 완료"
                                , Toast.LENGTH_SHORT).show();

                        mInteractor.pushMessage(mPushText.getText().toString(), mContents.getId());

                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext()
                                , "포스트 수정 실패: 관리자 문의"
                                , Toast.LENGTH_SHORT).show();

                        getActivity().finish();
                    }
                }
            });
        }
    }

    private void postOut() {
        mInteractor.remove(mContents.getId(), new ContentsInteractor.OnCommonListener() {
            @Override
            public void onReceived(Object obj) {
                if ((Boolean)obj) {
                    Toast.makeText(getActivity()
                            , mContents.getTitle() + " 삭제 완료"
                            , Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity()
                            , "삭제 실패: 관리자 문의 필요"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

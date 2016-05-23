package com.douncoding.readingsalon;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.douncoding.readingsalon.data.Contents;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WriteFragment extends Fragment {
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

    Contents mContents = new Contents();
    ContentsAdapter mPreviewAdapter;

    public static WriteFragment newInstance(String param) {
        WriteFragment fragment = new WriteFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PARAMS1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreviewAdapter = new ContentsAdapter();
        mSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.planets_array));

        String json = getArguments().getString(EXTRA_PARAMS1);
        mContents = new Gson().fromJson(json, Contents.class);
        Log.e(TAG, "CHECK:" + new Gson().toJson(mContents));
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

                // 공지사항 인 경우만 현시
                if (position == (mSpinnerAdapter.getCount() - 1))
                    mExpandView.setVisibility(View.VISIBLE);
                else
                    mExpandView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinner.setSelection(0);

        mTitleText = (EditText)rootView.findViewById(R.id.form_title);
        mContentText = (EditText)rootView.findViewById(R.id.form_content);
        mSubjectText = (EditText)rootView.findViewById(R.id.form_subject);
        mOverviewText = (EditText)rootView.findViewById(R.id.form_overview);
        mPushText = (EditText)rootView.findViewById(R.id.form_push);

        mSelectImage = (Button)rootView.findViewById(R.id.form_select_image);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPreviewRecyclerView = (RecyclerView)rootView.findViewById(R.id.form_preview);
        mPreviewRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mPreviewRecyclerView.setAdapter(mPreviewAdapter);

        mPostIn = (Button)rootView.findViewById(R.id.post_in);
        mPostIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContents.setTitle(mTitleText.getText().toString());
                mContents.setContent(mContentText.getText().toString());
                mContents.setSubject(mSubjectText.getText().toString());
                mContents.setOverview(mOverviewText.getText().toString());

                if (!TextUtils.isEmpty(mContents.getTitle())
                        && !TextUtils.isEmpty(mContents.getContent())) {

                    String local;
                    try {
                        local = getAbsolutePathFromUri(Uri.parse(mContents.getImage()));
                    } catch (NullPointerException e) {
                        local = null;
                    }

                    /*
                    if (isModify) {
                        Log.i(TAG, "포스트 수정");
                        mContents.setImage(local);
                        presenter.modifyContents(mContents, mPushText.getText().toString());
                    } else {
                        Log.i(TAG, "포스트 작성");
                        presenter.writeContents(mContents.getTitle()
                                , mContents.getContent()
                                , local
                                , mContents.getType()
                                , mContents.getSubject(), mContents.getOverview()
                                , mPushText.getText().toString());
                    }
                    */
                } else {
                    Toast.makeText(getActivity()
                            , "제목과 내용은 꼭 입력해야 합니다. 다시 확인하세요."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPostOut = (Button)rootView.findViewById(R.id.post_out);
        mPostOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //presenter.deleteContents(mContents.getId());
            }
        });

        /*
        if (!isModify)
            mPostOut.setVisibility(View.GONE);
        */

        if (mContents != null) {
            mSpinner.setSelection(mContents.getType());
            mTitleText.setText(mContents.getTitle());
            mContentText.setText(mContents.getContent());

            if (mContents.getSubject() != null)
                mSubjectText.setText(mContents.getSubject());

            if (mContents.getOverview() != null)
                mOverviewText.setText(mContents.getOverview());
        }

        return rootView;
    }

    public void setPhoto(Uri uri) {
        mContents.setTitle(mTitleText.getText().toString());
        mContents.setContent(mContentText.getText().toString());
        mContents.setSubject(mSubjectText.getText().toString());
        mContents.setOverview(mOverviewText.getText().toString());
        mContents.setImage(uri.toString());
        //mContents.setSamples(true);

        List<Contents> items = new ArrayList<>();
        items.add(mContents);
        items.add(mContents);

        //mPreviewAdapter.clearItem();
        //mPreviewAdapter.addItems(items);
    }

    public String getAbsolutePathFromUri(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
    }

    public void init() {
        mTitleText.setText("");
        mContentText.setText("");
        mOverviewText.setText("");
        mPushText.setText("");
        mSubjectText.setText("");
        mContentText.setText("");
    }
}

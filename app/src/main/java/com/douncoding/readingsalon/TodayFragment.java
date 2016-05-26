package com.douncoding.readingsalon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.douncoding.readingsalon.controller.ContentsInteractor;
import com.douncoding.readingsalon.data.Contents;
import com.douncoding.readingsalon.view.EndlessRecyclerViewScrollListener;

import java.util.List;

public class TodayFragment extends Fragment {
    public static final String TAG = TodayFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    ContentsAdapter mAdapter;

    ContentsInteractor mInteractor;

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new ContentsAdapter();
        mAdapter.setOnItemClickListener(new ContentsAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Utils.navigateToDetailActivity(getContext(), mAdapter.mDataSet.get(position).getId());
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, String.format("onLoadMode: page:%d total:%d", page, totalItemsCount));
                mInteractor.load(ContentsType.TODAY, totalItemsCount, ContentsInteractor.DEFAULT_LOAD_COUNT);
            }
        });

        mInteractor = new ContentsInteractor(getContext().getApplicationContext());
        mInteractor.setOnListener(new ContentsInteractor.OnListener() {
            @Override
            public void onLoad(ContentsType type, List<Contents> items) {
                if (mAdapter != null)
                    mAdapter.add(items);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView = null;
        mLayoutManager = null;
        mAdapter = null;
        mInteractor = null;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        mInteractor.load(ContentsType.TODAY, 0, ContentsInteractor.DEFAULT_LOAD_COUNT);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.clear();
    }
}

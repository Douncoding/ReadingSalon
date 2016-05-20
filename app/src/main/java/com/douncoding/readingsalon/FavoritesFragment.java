package com.douncoding.readingsalon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.douncoding.readingsalon.data.Contents;

import java.util.List;

public class FavoritesFragment extends Fragment {
    public static final String TAG = FavoritesFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    ContentsAdapter mAdapter;

    AppContext mApp;
    ContentsInteractor mInteractor;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mApp = (AppContext)context.getApplicationContext();
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
                Utils.navigateToDetailActivity(getContext(), mAdapter.get(position));
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, String.format("onLoadMode: page:%d total:%d", page, totalItemsCount));
                mInteractor.load(ContentsType.FAVOR, totalItemsCount, ContentsInteractor.DEFAULT_LOAD_COUNT);
            }
        });


        mInteractor = new ContentsInteractor(getContext().getApplicationContext());
        mInteractor.setOnListener(new ContentsInteractor.OnListener() {
            @Override
            public void onLoad(ContentsType type, List<Contents> items) {
                mAdapter.add(items);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (!mApp.mOwner.isLogin()) {
                Log.d(TAG, "담기 목록 조회: 로그인 필요");
                Utils.navigateToSignActivity(getContext());
            } else {

            }
        } else {

        }
    }
}

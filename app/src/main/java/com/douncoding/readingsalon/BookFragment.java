package com.douncoding.readingsalon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.douncoding.readingsalon.data.Contents;

import java.util.List;


public class BookFragment extends Fragment {
    public static final String TAG = BookFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    ContentsAdapter mAdapter;

    AppContext mApp;
    ContentsInteractor mInteractor;

    public static BookFragment newInstance() {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new ContentsAdapter();
        mAdapter.setOnItemClickListener(new ContentsAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Utils.navigateToDetailActivity(getContext(), );
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, String.format("onLoadMode: page:%d total:%d", page, totalItemsCount));
                mInteractor.load(ContentsType.BOOK, totalItemsCount, ContentsInteractor.DEFAULT_LOAD_COUNT);
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

        mRecyclerView = null;
        mAdapter = null;
        mLayoutManager = null;
        mInteractor = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mInteractor.load(ContentsType.BOOK, 0, ContentsInteractor.DEFAULT_LOAD_COUNT);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.clear();
    }
}

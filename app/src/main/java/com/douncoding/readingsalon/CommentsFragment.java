package com.douncoding.readingsalon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.douncoding.readingsalon.controller.CommentInteractor;
import com.douncoding.readingsalon.data.Comment;
import com.douncoding.readingsalon.data.Owner;
import com.douncoding.readingsalon.view.SimpleDividerItemDecoration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = CommentsFragment.class.getSimpleName();

    EditText mEditText;
    RecyclerView mRecyclerView;
    ImageView mExpendedFunc;
    TextView mPostView;

    LinearLayoutManager mLayoutManager;
    CommentAdapter mAdapter;
    CommentInteractor mInteractor;

    int mContentsId;
    Owner owner;

    public static CommentsFragment newInstance(int contentsId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putInt("contentsId", contentsId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentsId = getArguments().getInt("contentsId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        mEditText = (EditText)rootView.findViewById(R.id.comment_edit);
        mPostView = (TextView)rootView.findViewById(R.id.comment_post);
        mPostView.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CommentAdapter();

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        mInteractor = new CommentInteractor(getContext());
        mInteractor.setOnListener(onActionListener);

        owner = new Owner(getContext());
        return rootView;
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

        mInteractor.read(mContentsId);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_post:
                sendComment(mEditText.getText().toString());
                break;
        }
    }

    private void sendComment(String content) {
        if (content == null || content.length() == 0) {
            return ;
        }

        Comment comment = new Comment();
        comment.setCid(mContentsId);
        comment.setContent(content);
        comment.setMid(owner.getId());

        if (owner.isLogin()) {
            InputMethodManager mInputMethodManager =
                    (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(mPostView.getWindowToken(), 0);
            mInteractor.post(comment);
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title("사용 제한")
                    .content("로그인이 필요한 서비스입니다.")
                    .positiveText(android.R.string.ok)
                    .show();
        }
    }

    CommentInteractor.OnListener onActionListener = new CommentInteractor.OnListener() {
        @Override
        public void onReaded(List<Comment> comments) {
            if (comments == null || comments.size() == 0) {
                return ;
            }

            mAdapter.addItems(comments);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }

        @Override
        public void onCreated(Comment comment) {
            mEditText.setText("");
            mAdapter.addItem(comment);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }

        @Override
        public void onDeleted(Comment comment) {
            mAdapter.delItem(comment);
        }

        @Override
        public void onUpdated(Comment comment) {

        }

        @Override
        public void onError() {

        }
    };

    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.DataHolder> {
        ArrayList<Comment> mDataset = new ArrayList<>();

        public class DataHolder extends RecyclerView.ViewHolder {
            TextView mNameView;
            TextView mTextView;
            TextView mTimeView;
            ImageView mCloseView;

            public DataHolder(View itemView) {
                super(itemView);

                mNameView = (TextView)itemView.findViewById(R.id.item_comment_name);
                mTextView = (TextView)itemView.findViewById(R.id.item_comment_text);
                mTimeView = (TextView)itemView.findViewById(R.id.item_comment_time);
                mCloseView = (ImageView)itemView.findViewById(R.id.item_comment_close);
                mCloseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInteractor.remove(mAdapter.get(getPosition()));
                    }
                });
            }
        }

        @Override
        public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_comment_basic_row, parent, false);

            return new DataHolder(view);
        }

        @Override
        public void onBindViewHolder(DataHolder holder, int position) {
            Comment comment = mDataset.get(position);

            holder.mNameView.setText(comment.getName());
            holder.mTextView.setText(comment.getContent());
            holder.mTimeView.setText(comment.getTime());

            if (comment.getMemberId() != new Owner(getContext()).load().getId()) {
                holder.mCloseView.setVisibility(View.GONE);
            } else {
                holder.mCloseView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void addItem(Comment comment) {
            int pos = mDataset.size();
            mDataset.add(pos, comment);
            notifyItemInserted(pos);
        }

        public void delItem(Comment comment) {
            int position = -1;

            for(Comment item : mDataset) {
                position++;
                if (item.getId() == comment.getId())
                    break;
            }

            if (position >  0 && position < mDataset.size()) {
                notifyItemRemoved(position);
                mDataset.remove(position);
            }
        }

        public void addItems(List<Comment> commentList) {
            mDataset.addAll(commentList);
            notifyDataSetChanged();
        }

        public Comment get(int position) {
            return mDataset.get(position);
        }
    }
}

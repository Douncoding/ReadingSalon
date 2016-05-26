package com.douncoding.readingsalon;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.douncoding.readingsalon.data.Contents;

import java.util.ArrayList;
import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.DataHolder> {
    public static final String TAG = ContentsAdapter.class.getSimpleName();
    private enum CardType {BASIC, OVER, BLUR, BLIND}

    Context context;
    ArrayList<Contents> mDataSet;

    public class DataHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView mImageView;
        TextView mTitleView;
        TextView mSubjectText;
        TextView mOverviewText;

        public DataHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView)itemView.findViewById(R.id.today_picture);
            mTitleView = (TextView)itemView.findViewById(R.id.today_title);
            mSubjectText = (TextView)itemView.findViewById(R.id.today_header_main);
            mOverviewText = (TextView)itemView.findViewById(R.id.today_header_sub);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onItemClick(int position, View v);
    }

    public ContentsAdapter() {
        mDataSet = new ArrayList<>();
    }

    @Override
    public ContentsAdapter.DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == CardType.OVER.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_content_over_row, parent, false);
        } else if (viewType == CardType.BLUR.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_content_blur_row, parent, false);
        } else if (viewType == CardType.BLIND.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_content_blind_row, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_content_basic_row, parent, false);
        }

        context = parent.getContext();
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContentsAdapter.DataHolder holder, int position) {
        Contents contents = mDataSet.get(position);

        holder.mTitleView.setText(contents.getTitle());

        switch (CardType.values()[getItemViewType(position)]) {
            case BASIC:
            case OVER:
                drawDirectImage(contents.getImage(), holder.mImageView);
                break;
            case BLUR:
                drawBlurImage(contents.getImage(), holder.mImageView);
                break;
            case BLIND:
                drawBlindImage(contents.getImage(), holder.mImageView);
                holder.mSubjectText.setText(contents.getSubject());
                holder.mOverviewText.setText(contents.getOverview());
                break;
        }
    }

    private void drawDirectImage(String filename, final ImageView view) {
        String uri;

        if (Utils.isLocalMedia(filename)) {
            uri = filename;
        } else {
            uri = Utils.getServerImage(filename);
        }

        Glide.with(context)
                .load(uri)
                .asBitmap()
                .placeholder(R.drawable.image_loading_animate)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        view.setImageBitmap(resource);
                    }
                });
    }

    private void drawBlurImage(String filename, final ImageView view) {
        String uri;

        if (Utils.isLocalMedia(filename)) {
            uri = filename;
        } else {
            uri = Utils.getServerImage(filename);
        }

        Glide.with(context)
                .load(uri)
                .asBitmap()
                .placeholder(R.drawable.image_loading_animate)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        view.setImageBitmap(Utils.buildBlurImage(view, resource));
                    }
                });
    }

    private void drawBlindImage(String filename, final ImageView view) {
        String uri;

        if (Utils.isLocalMedia(filename)) {
            uri = filename;
        } else {
            uri = Utils.getServerImage(filename);
        }

        Glide.with(context)
                .load(uri)
                .asBitmap()
                .placeholder(R.drawable.image_loading_animate)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        view.setImageBitmap(Utils.buildBlindImage(view, resource));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getType();
    }

    public void add(List<Contents> items) {
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public Contents get(int position) {
        return mDataSet.get(position);
    }
}

package com.douncoding.readingsalon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.douncoding.readingsalon.data.ConfigMenu;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.DataHolder> {
    Context context;
    ArrayList<ConfigMenu> mDataset = new ArrayList<>();

    public class DataHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        ImageView mImageView;
        TextView mTitleView;
        TextView mExpendView;
        Switch mASwitch;

        public DataHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView)itemView.findViewById(R.id.item_config_icon);
            mTitleView = (TextView)itemView.findViewById(R.id.item_config_title);
            mExpendView = (TextView)itemView.findViewById(R.id.item_config_expend);
            mASwitch = (Switch)itemView.findViewById(R.id.item_config_push);

            if (mASwitch != null) {
                mASwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (onSwitchedCallbacks != null)
                            onSwitchedCallbacks.onSwitched(isChecked);
                    }
                });
            }

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

    private OnSwitchedCallbacks onSwitchedCallbacks;

    public interface OnSwitchedCallbacks {
        void onSwitched(boolean isChecked);
    }

    public void setOnSwitchedCallbacks(OnSwitchedCallbacks onSwitchedCallbacks) {
        this.onSwitchedCallbacks = onSwitchedCallbacks;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case ConfigMenu.TYPE_TOGGLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_config_toggle_row, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_config_basic_row, parent, false);
                break;
        }

        context = parent.getContext();

        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        ConfigMenu data = mDataset.get(position);

        holder.mTitleView.setText(data.getTitle());

        if (data.getExpend() != null)
            holder.mExpendView.setText(data.getExpend());

        Glide.with(context)
                .load(data.getIconRes())
                .into(holder.mImageView);

        switch (getItemViewType(position)) {
            case ConfigMenu.TYPE_TOGGLE:
                holder.mASwitch.setChecked(data.isPush());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        ConfigMenu data = mDataset.get(position);
        return data.getType();
    }

    public void addMenus(ConfigMenu[] menus) {
        mDataset.addAll(Arrays.asList(menus));
        notifyDataSetChanged();
    }

    public void update(ConfigMenu item) {
        for (int i = 0; i < mDataset.size(); i++) {
            ConfigMenu menu = mDataset.get(i);
            if (menu.getIconRes() == item.getIconRes()) {
                mDataset.set(i, item);
                notifyItemChanged(i);
            }
        }
    }

    public ConfigMenu get(int iconRes) {
        for (ConfigMenu config : mDataset) {
            if (config.getIconRes() == iconRes) {
                return config;
            }
        }
        return null;
    }
}

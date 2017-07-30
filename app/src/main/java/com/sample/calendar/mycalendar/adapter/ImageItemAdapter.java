package com.sample.calendar.mycalendar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sample.calendar.mycalendar.R;

import java.util.List;

/**
 * author: ${user}
 * Created by Mr.fan on 2017/7/30.
 * descrition: You share rose get fun. *_* *_*
 */

public class ImageItemAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Integer> mImageList;
    private final LayoutInflater mLayoutInflater;

    public ImageItemAdapter(Context context, List<Integer> imageList) {
        this.mContext = context;
        this.mImageList = imageList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mLayoutInflater.inflate(R.layout.item_image_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).bindHolder(position, mImageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mIv;

        public ItemHolder(View itemView) {
            super(itemView);
            this.mIv = (ImageView) itemView.findViewById(R.id.iv);

            itemView.setOnClickListener(this);
        }

        public void bindHolder(int position, Integer integer) {
            mIv.setImageResource(integer);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mIv, getAdapterPosition(), mImageList.get(getAdapterPosition()));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, int position, int imageResource);
    }
}

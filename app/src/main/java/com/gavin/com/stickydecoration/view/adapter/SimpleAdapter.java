package com.gavin.com.stickydecoration.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;
import com.gavin.com.stickydecoration.util.DensityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gavin
 * Created date 17/6/5
 * Created log
 */

public class SimpleAdapter extends RecyclerView.Adapter {
    private List<City> mCities;
    private Context mContext;

    public SimpleAdapter(Context context, List<City> cities) {
        mCities = cities;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        if (mCities.size() > position) {
            City city = mCities.get(position);
            if (city.isExpanded()) {
                ViewGroup.LayoutParams layoutParams = holder.mLlBg.getLayoutParams();
                layoutParams.height = DensityUtil.dip2px(mContext, 100);
                holder.mLlBg.setLayoutParams(layoutParams);
                //holder.itemView.setVisibility(View.VISIBLE);
                int i = position % 5 + 1;
                if (i == 1) {
                    holder.mIvCity.setImageResource(R.mipmap.subject1);
                    holder.mLlBg.setBackgroundColor(mContext.getResources().getColor(R.color.bg1));
                } else if (i == 2) {
                    holder.mIvCity.setImageResource(R.mipmap.subject2);
                    holder.mLlBg.setBackgroundColor(mContext.getResources().getColor(R.color.bg2));
                } else if (i == 3) {
                    holder.mIvCity.setImageResource(R.mipmap.subject3);
                    holder.mLlBg.setBackgroundColor(mContext.getResources().getColor(R.color.bg3));
                } else if (i == 4) {
                    holder.mIvCity.setImageResource(R.mipmap.subject4);
                    holder.mLlBg.setBackgroundColor(mContext.getResources().getColor(R.color.bg4));
                } else {
                    holder.mIvCity.setImageResource(R.mipmap.subject5);
                    holder.mLlBg.setBackgroundColor(mContext.getResources().getColor(R.color.bg5));
                }
                holder.mTvCity.setText(city.getName());
            } else {
                ViewGroup.LayoutParams layoutParams = holder.mLlBg.getLayoutParams();
                layoutParams.height = DensityUtil.dip2px(mContext, 0);
                holder.mLlBg.setLayoutParams(layoutParams);
                //holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_city)
        ImageView mIvCity;
        @BindView(R.id.tv_city)
        TextView mTvCity;
        @BindView(R.id.tv_brief)
        TextView mTvBrief;
        @BindView(R.id.ll_bg)
        LinearLayout mLlBg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

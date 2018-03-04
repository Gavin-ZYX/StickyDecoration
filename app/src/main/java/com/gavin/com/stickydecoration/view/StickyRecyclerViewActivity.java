package com.gavin.com.stickydecoration.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gavin.com.library.StickyDecoration;
import com.gavin.com.library.listener.GroupListener;
import com.gavin.com.library.listener.OnGroupClickListener;
import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;
import com.gavin.com.stickydecoration.util.CityUtil;
import com.gavin.com.stickydecoration.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文字悬浮
 */
public class StickyRecyclerViewActivity extends AppCompatActivity {
    // TODO: gavin 2018/2/9 已知问题： notifyItemRemoved notifyItemRangeChanged时，界面渲染闪烁问题

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;

    RecyclerView.Adapter mAdapter;
    List<City> dataList = new ArrayList<>();
    private String TAG = "StickyRecyclerViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_recycler_view);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //模拟数据
        dataList.addAll(CityUtil.getCityList());
        dataList.addAll(CityUtil.getCityList());

        //------------- StickyDecoration 使用部分  ----------------
        StickyDecoration decoration = StickyDecoration.Builder
                .init(new GroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //组名回调
                        if (dataList.size() > position && position > -1) {
                            //获取组名，用于判断是否是同一组
                            return dataList.get(position).getProvince();
                        }
                        return null;
                    }
                })
                .setGroupBackground(Color.parseColor("#48BDFF"))        //背景色
                .setGroupHeight(DensityUtil.dip2px(this, 35))     //高度
                .setDivideColor(Color.parseColor("#EE96BC"))            //分割线颜色
                .setDivideHeight(DensityUtil.dip2px(this, 2))     //分割线高度 (默认没有分割线)
                .setGroupTextColor(Color.BLACK)                                    //字体颜色 （默认）
                .setGroupTextSize(DensityUtil.sp2px(this, 15))    //字体大小
                .setTextSideMargin(DensityUtil.dip2px(this, 10))  // 边距   靠左时为左边距  靠右时为右边距
                .setOnClickListener(new OnGroupClickListener() {                   //点击事件，返回当前分组下的第一个item的position
                    @Override
                    public void onClick(int position) {                                 //Group点击事件
                        String content = "onGroupClick --> " + dataList.get(position).getProvince();
                        Toast.makeText(StickyRecyclerViewActivity.this, content, Toast.LENGTH_LONG).show();
                    }
                })
                .build();
        //------------- StickyDecoration 使用部分  ----------------
        //下面是平时的RecyclerView操作

        RecyclerView.LayoutManager manager;
        String type = getIntent().getStringExtra("type");
        if (TextUtils.equals(type, "grid")) {
            manager = new GridLayoutManager(this, 3);
        } else {
            manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        if (TextUtils.equals(type, "grid")) {
        }
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(decoration);

        mAdapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
                Holder holder = (Holder) viewHolder;
                holder.mTextView.setText(dataList.get(position).getName());
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    static class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv)
        TextView mTextView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ---------  忽略下面的代码  --------------

    final int position = 3;

    public void onAdd(View v) {
        int previousSize = dataList.size();
        List<City> list = CityUtil.getCityList();
        dataList.addAll(list);
        mAdapter.notifyItemRangeInserted(previousSize, list.size());
        mAdapter.notifyItemRangeChanged(previousSize, list.size());
    }

    public void onDelete(View v) {
        dataList.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, dataList.size() - 3);

    }

    public void onDeleteLast(View v) {
        int endPosition = dataList.size() -1;
        dataList.remove(endPosition);
        mAdapter.notifyItemRemoved(endPosition);
        mAdapter.notifyItemChanged(endPosition);
    }
}

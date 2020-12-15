package com.gavin.com.stickydecoration.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gavin.com.library.StickyDecoration;
import com.gavin.com.library.listener.GroupListener;
import com.gavin.com.library.listener.OnGroupClickListener;
import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;
import com.gavin.com.stickydecoration.util.CityUtil;
import com.gavin.com.stickydecoration.util.DensityUtil;
import com.gavin.com.stickydecoration.view.widget.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * 文字悬浮
 */
public class StickyActivity extends AppCompatActivity {
    // TODO: gavin 2018/2/9 已知问题： notifyItemRemoved notifyItemRangeChanged时，界面渲染闪烁问题

    MyRecyclerView mRecyclerView;

    RecyclerView.Adapter mAdapter;
    List<City> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_recycler_view);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv);
        //模拟数据
        dataList.addAll(CityUtil.getCityList());
        dataList.addAll(CityUtil.getCityList());

        //------------- StickyDecoration 使用部分  ----------------
        StickyDecoration.Builder builder = StickyDecoration.Builder
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
                //背景色
                .setGroupBackground(Color.parseColor("#48BDFF"))
                //高度
                .setGroupHeight(DensityUtil.dip2px(this, 35))
                //分割线颜色
                .setDivideColor(Color.parseColor("#EE96BC"))
                //分割线高度 (默认没有分割线)
                .setDivideHeight(DensityUtil.dip2px(this, 2))
                //字体颜色 （默认）
                .setGroupTextColor(Color.BLACK)
                //字体大小
                .setGroupTextSize(DensityUtil.sp2px(this, 15))
                // 边距   靠左时为左边距  靠右时为右边距
                .setTextSideMargin(DensityUtil.dip2px(this, 10))
                // header数量（默认0）
                //.setHeaderCount(1)
                //Group点击事件
                .setOnClickListener(new OnGroupClickListener() {
                    @Override
                    public void onClick(int position, int id) {
                        //点击事件，返回当前分组下的第一个item的position
                        String content = "onGroupClick --> " + position + " " +  dataList.get(position).getProvince();
                        Toast.makeText(StickyActivity.this, content, Toast.LENGTH_SHORT).show();
                    }
                });
                //.setSticky(false)
        //------------- StickyDecoration 使用部分  ----------------
        //下面是平时的RecyclerView操作

        mAdapter = new QuickAdapter();
        ((QuickAdapter) mAdapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                City city = dataList.get(position);
                Toast.makeText(StickyActivity.this,
                        "item click " + position + " : " + city.getProvince() + " - " + city.getName(),
                        1000).show();
            }
        });

        RecyclerView.LayoutManager manager;
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // header
        TextView header = new TextView(this);
        header.setText("header");
        header.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        header.setLayoutParams(params);
        //((QuickAdapter) mAdapter).addHeaderView(header);

        StickyDecoration decoration = builder.build();
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    class QuickAdapter extends BaseQuickAdapter<City, BaseViewHolder> {
        public QuickAdapter() {
            super(R.layout.item_recycler_view, dataList);
        }

        @Override
        protected void convert(@Nullable BaseViewHolder holder, City item) {
            holder.setText(R.id.tv, item.getName());
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
        int endPosition = dataList.size() - 1;
        dataList.remove(endPosition);
        mAdapter.notifyItemRemoved(endPosition);
        mAdapter.notifyItemChanged(endPosition);
    }

    public void onRefresh(View v) {
        dataList.clear();
        dataList.addAll(CityUtil.getRandomCityList());
        mAdapter.notifyDataSetChanged();
    }

    public void onClean(View v) {
        dataList.clear();
        mAdapter.notifyDataSetChanged();
    }
}

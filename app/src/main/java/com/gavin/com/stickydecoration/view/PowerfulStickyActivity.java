package com.gavin.com.stickydecoration.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gavin.com.library.PowerfulStickyDecoration;
import com.gavin.com.library.StickyDecoration;
import com.gavin.com.library.listener.OnGroupClickListener;
import com.gavin.com.library.listener.PowerGroupListener;
import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;
import com.gavin.com.stickydecoration.util.CityUtil;
import com.gavin.com.stickydecoration.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义View悬浮
 */
public class PowerfulStickyActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    RecyclerView.Adapter mAdapter;
    List<City> dataList = new ArrayList<>();
    PowerfulStickyDecoration decoration;

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

        //------------- PowerfulStickyDecoration 使用部分  ----------------
        PowerGroupListener listener = new PowerGroupListener() {
            @Override
            public String getGroupName(int position) {
                //获取组名，用于判断是否是同一组
                if (dataList.size() > position) {
                    return dataList.get(position).getProvince();
                }
                return null;
            }

            @Override
            public View getGroupView(int position) {
                //获取自定定义的组View
                if (dataList.size() > position) {
                    View view = getLayoutInflater().inflate(R.layout.item_group, null, false);
                    ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
                    return view;
                } else {
                    return null;
                }
            }
        };
        decoration = PowerfulStickyDecoration.Builder
                .init(listener)
                .setGroupHeight(DensityUtil.dip2px(this, 40))
                .setGroupBackground(Color.parseColor("#48BDFF"))
                .setDivideColor(Color.parseColor("#27ad9a"))
                .setDivideHeight(DensityUtil.dip2px(this, 1))
                .setCacheEnable(true)
                .setHeaderCount(1)
                .setOnClickListener(new OnGroupClickListener() {
                    @Override
                    public void onClick(int position, int id) {                                 //Group点击事件
                        String content = "onGroupClick --> " + dataList.get(position).getProvince() + "   id --> " + id;
                        showToast(content);
                    }
                })
                .build();
        //-------------                  ----------------
        //下面是平时的RecyclerView操作

        mAdapter = new QuickAdapter();
        ((QuickAdapter) mAdapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                City city = dataList.get(position);
                Toast.makeText(PowerfulStickyActivity.this,
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
        ((QuickAdapter) mAdapter).addHeaderView(header);

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


    public void onRefresh(View v) {
        dataList.clear();
        dataList.addAll(CityUtil.getRandomCityList());
        mAdapter.notifyDataSetChanged();
        decoration.clearCache();
    }

    private void showToast(String content) {
        Toast.makeText(PowerfulStickyActivity.this, content, Toast.LENGTH_LONG).show();
    }

    private void l(String str) {
        Log.i("TAG", str);
    }
}

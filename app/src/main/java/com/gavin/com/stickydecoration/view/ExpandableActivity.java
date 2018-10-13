package com.gavin.com.stickydecoration.view;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.com.library.PowerfulStickyDecoration;
import com.gavin.com.library.listener.OnGroupClickListener;
import com.gavin.com.library.listener.PowerGroupListener;
import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;
import com.gavin.com.stickydecoration.util.CityUtil;
import com.gavin.com.stickydecoration.util.DensityUtil;
import com.gavin.com.stickydecoration.view.adapter.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可展开的recyclerview View悬浮
 */
public class ExpandableActivity extends ActionBarActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;

    RecyclerView.Adapter mAdapter;
    List<City> dataList = new ArrayList<>();
    PowerfulStickyDecoration decoration;

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

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(manager);
        //------------- PowerfulStickyDecoration 使用部分  ----------------
        decoration = PowerfulStickyDecoration.Builder
                .init(new PowerGroupListener() {
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
                            final View view = getLayoutInflater().inflate(R.layout.city_group, null, false);
                            ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv);
                            imageView.setImageResource(dataList.get(position).getIcon());
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                .setCacheEnable(true)
                .setGroupHeight(DensityUtil.dip2px(ExpandableActivity.this, 40))
                .setOnClickListener(new OnGroupClickListener() {
                    @Override
                    public void onClick(int position, int id) {
                        if (dataList.size() > position) {
                            //修改数据
                            changeExpandedState(position);
                            City city = dataList.get(position);
                            //修改悬浮窗
                            final View view = getLayoutInflater().inflate(R.layout.city_group, null, false);
                            ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv);
                            imageView.setImageResource(dataList.get(position).getIcon());
                            ImageView ivExpanded = (ImageView) view.findViewById(R.id.iv_expanded);
                            int rotation = city.isExpanded() ? 0 : 180;
                            ivExpanded.setRotation(rotation);
                            //修改数据后，刷新指定的悬浮窗
                            decoration.notifyRedraw(mRv, view, position);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .build();
        //----------------                 -------------
        //下面是平时的RecyclerView操作
        mRv.addItemDecoration(decoration);
        mAdapter = new SimpleAdapter(this, dataList);
        mRv.setAdapter(mAdapter);
    }

    /**
     * 修改数据
     *
     * @param position
     */
    private void changeExpandedState(int position) {
        if (dataList.size() > position) {
            City city = dataList.get(position);
            city.setExpanded(!city.isExpanded());
            position++;
            if (dataList.size() > position) {
                //下个是当前分组
                City city2 = dataList.get(position);
                if (TextUtils.equals(city.getProvince(), city2.getProvince())) {
                    changeExpandedState(position);
                }
            }
        }
    }

    public void onRefresh(View v) {

    }
}

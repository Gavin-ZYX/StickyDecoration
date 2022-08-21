package com.gavin.com.stickydecoration.view;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.gavin.com.stickydecoration.view.widget.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * 文字悬浮
 */
public class StickyGridActivity extends AppCompatActivity {
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
                .setGroupBackground(Color.parseColor("#48BDFF"))
                .setGroupHeight(DensityUtil.dip2px(this, 35))
                .setDivideColor(Color.parseColor("#EE96BC"))
                .setDivideHeight(DensityUtil.dip2px(this, 2))
                .setGroupTextColor(Color.BLACK)
                .setGroupTextSize(DensityUtil.sp2px(this, 15))
                .setTextSideMargin(DensityUtil.dip2px(this, 10))
                .setOnClickListener(new OnGroupClickListener() {
                    @Override
                    public void onClick(int position, int id) {                                 //Group点击事件
                        String content = "onGroupClick --> " + dataList.get(position).getProvince();
                        Toast.makeText(StickyGridActivity.this, content, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        //------------- StickyDecoration 使用部分  ----------------
        //下面是平时的RecyclerView操作

        RecyclerView.LayoutManager manager;
        manager = new GridLayoutManager(this, 3);
        decoration.resetSpan(mRecyclerView, (GridLayoutManager) manager);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(decoration);

        mAdapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
                Holder holder = (Holder) viewHolder;
                holder.mTextView.setText(dataList.get(position).getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(StickyGridActivity.this, "item click " + position, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public Holder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv);
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

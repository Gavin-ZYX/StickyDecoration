package com.gavin.com.stickydecoration.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.apmtools.APMManager;
import com.gavin.com.library.PowerfulStickyDecoration;
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
 * 自定义View悬浮
 */
public class BeautifulRecyclerViewActivity extends AppCompatActivity {

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
        initApm();
    }

    private void initView() {
        //模拟数据
        dataList.addAll(CityUtil.getCityList());
        dataList.addAll(CityUtil.getCityList());

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
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
                    public View getGroupView(final int position) {
                        //获取自定定义的组View
                        if (dataList.size() > position) {
                            final View view = getLayoutInflater().inflate(R.layout.city_group, null, false);
                            ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv);
                            imageView.setImageResource(dataList.get(position).getIcon());
                            // TODO: gavin 2018/3/2 模拟网络加载图片
                            //asyncLoadImage(view, decoration, position);
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                .setCacheEnable(true)
                //.resetSpan(mRv, gridLayoutManager)
                .setGroupHeight(DensityUtil.dip2px(BeautifulRecyclerViewActivity.this, 80))   //设置高度
                .build();
        //设置为强引用
        //decoration.setStrongReference(true);
        //----------------                 -------------
        //下面是平时的RecyclerView操作
        mRv.addItemDecoration(decoration);
        mAdapter = new SimpleAdapter(this, dataList);
        mRv.setAdapter(mAdapter);
    }

    private void initApm() {
        APMManager.getInstance(this).showAPM();
    }


    /**
     * 模拟网络加载图
     *
     * @param decoration
     * @param position
     */
    private void asyncLoadImage(final View view, final PowerfulStickyDecoration decoration, final int position) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
                ImageView imageView = (ImageView) view.findViewById(R.id.iv);
                imageView.setImageResource(dataList.get(position).getIcon());
                if (decoration != null) {
                    decoration.notifyRedraw(mRv, view, position);
                }
            }
        }.execute();
    }
}

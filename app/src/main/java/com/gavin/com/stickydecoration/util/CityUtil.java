package com.gavin.com.stickydecoration.util;

import com.gavin.com.stickydecoration.R;
import com.gavin.com.stickydecoration.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gavin
 * Created date 17/5/31
 * Created log
 */

public class CityUtil {

    private static final String[] CITYS = {"福建省", "安徽省", "浙江省", "江苏省"};

    /**
     * 获取城市名
     *
     * @return
     */
    public static List<City> getCityList() {
        List<City> dataList = new ArrayList<>();
        final String FU_JIAN = CITYS[0];
        final int FU_JIAN_ICON = R.mipmap.city1;
        dataList.add(new City("福州", FU_JIAN, FU_JIAN_ICON));
        dataList.add(new City("厦门", FU_JIAN, FU_JIAN_ICON));
        dataList.add(new City("泉州", FU_JIAN, FU_JIAN_ICON));
        dataList.add(new City("宁德", FU_JIAN, FU_JIAN_ICON));
        dataList.add(new City("漳州", FU_JIAN, FU_JIAN_ICON));
        final String AN_HUI = CITYS[1];
        final int AN_HUI_ICON = R.mipmap.city2;
        dataList.add(new City("合肥", AN_HUI, AN_HUI_ICON));
        dataList.add(new City("芜湖", AN_HUI, AN_HUI_ICON));
        dataList.add(new City("蚌埠", AN_HUI, AN_HUI_ICON));
        final String ZHE_JIANG = CITYS[2];
        final int ZHE_JIANG_ICON = R.mipmap.city3;
        dataList.add(new City("杭州", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("宁波", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("温州", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("嘉兴", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("绍兴", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("金华", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("湖州", ZHE_JIANG, ZHE_JIANG_ICON));
        dataList.add(new City("舟山", ZHE_JIANG, ZHE_JIANG_ICON));
        final String JIANG_SU = CITYS[3];
        final int JIANG_SU_ICOM = R.mipmap.city4;
        dataList.add(new City("南京", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("苏州", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("徐州", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("南通", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("无锡", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("盐城", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("淮安", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("泰州", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("常州", JIANG_SU, JIANG_SU_ICOM));
        dataList.add(new City("连云港", JIANG_SU, JIANG_SU_ICOM));
        return dataList;
    }


    /**
     * 获取城市名
     *
     * @return
     */
    public static List<City> getRandomCityList() {
        List<City> dataList = new ArrayList<>();
        Random random = new Random();
        int provinceSize = random.nextInt(10) + 3;
        for (int i = 0; i < provinceSize; i++) {
            String province = getRandomCityName();
            int citySize = random.nextInt(10) + 1;
            for (int j = 0; j < citySize; j++) {
                dataList.add(new City(province + " : city " + j, province, R.mipmap.city4));
            }
        }
        return dataList;
    }

    private static String getRandomCityName() {
        Random random = new Random();
        return CITYS[random.nextInt(4)];
    }
}

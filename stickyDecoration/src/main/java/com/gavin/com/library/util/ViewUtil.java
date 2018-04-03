package com.gavin.com.library.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gavin
 * date 2018/4/1
 * View工具类
 */

public class ViewUtil {

    /**
     * 获取带用id的子view
     *
     * @param view
     */
    public static List<View> getChildViewWithId(View view) {
        List<View> list = new ArrayList<>();
        if (null != view && view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            LinkedList<ViewGroup> queue = new LinkedList<>();
            queue.add(viewGroup);
            while (!queue.isEmpty()) {
                ViewGroup current = queue.removeFirst();
                for (int i = 0; i < current.getChildCount(); i++) {
                    View childView = current.getChildAt(i);
                    if (childView instanceof ViewGroup) {
                        queue.addLast((ViewGroup) current.getChildAt(i));
                    }
                    if (childView.getId() != View.NO_ID) {
                        list.add(childView);
                    }
                }
            }
        }
        return list;
    }
}

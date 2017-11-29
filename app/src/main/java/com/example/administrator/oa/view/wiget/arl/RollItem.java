package com.example.administrator.oa.view.wiget.arl;

/**
 * Created by yinchao on 2016/07/19.
 * 功能：自定义轮播图中属性的封装
 */
public class RollItem {
    int picRes;
    String title;

    public RollItem(int picRes, String title) {
        this.picRes = picRes;
        this.title = title;
    }

    public int getPicPath() {
        return picRes;
    }

    public String getTitle() {
        return title;
    }
}

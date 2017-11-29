package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/6/27.
 */

public class LiuChongDingyiBean {
    int nameRes;
    int longNameRes;
    int iconRes;
    int type;

    public LiuChongDingyiBean(int nameRes, int longNameRes, int iconRes) {
        this.nameRes = nameRes;
        this.longNameRes = longNameRes;
        this.iconRes = iconRes;
    }

    public LiuChongDingyiBean(int nameRes, int longNameRes, int iconRes, int type) {
        this.nameRes = nameRes;
        this.longNameRes = longNameRes;
        this.iconRes = iconRes;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    public int getLongNameRes() {
        return longNameRes;
    }

    public void setLongNameRes(int longNameRes) {
        this.longNameRes = longNameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}

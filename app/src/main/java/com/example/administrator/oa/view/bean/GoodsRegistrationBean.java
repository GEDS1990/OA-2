package com.example.administrator.oa.view.bean;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/7/25.
 */

public class GoodsRegistrationBean implements Comparable<GoodsRegistrationBean>{
    String goods;
    String format;
    String num;
    String remarks;
    int index;


    public GoodsRegistrationBean(String goods, String format, String num, String remarks) {
        this.goods = goods;
        this.format = format;
        this.num = num;
        this.remarks = remarks;
    }

    public GoodsRegistrationBean(int index, String goods, String format, String num, String remarks) {
        this.index = index;
        this.goods = goods;
        this.format = format;
        this.num = num;
        this.remarks = remarks;
    }

    public GoodsRegistrationBean() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int compareTo(@NonNull GoodsRegistrationBean o) {
        int i = this.getIndex() - o.getIndex();//先按照年龄排序
//        if(i == 0){
//            return this.score - o.getScore();//如果年龄相等了再用分数进行排序
//        }
        return i;
    }
}

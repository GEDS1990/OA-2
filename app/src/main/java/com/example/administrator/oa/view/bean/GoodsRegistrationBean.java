package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/25.
 */

public class GoodsRegistrationBean {
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
}

package com.example.administrator.oa.view.bean;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ZijinDiaoboBean implements Comparable<ZijinDiaoboBean>{
    String fukuanAccount;
    String shoukuanAccount;
    String money;
    int index;

    public ZijinDiaoboBean(String fukuanAccount, String shoukuanAccount, String money) {
        this.fukuanAccount = fukuanAccount;
        this.shoukuanAccount = shoukuanAccount;
        this.money = money;
    }

    public ZijinDiaoboBean(int index, String fukuanAccount, String shoukuanAccount, String money) {
        this.fukuanAccount = fukuanAccount;
        this.shoukuanAccount = shoukuanAccount;
        this.money = money;
        this.index = index;
    }

    public String getFukuanAccount() {
        return fukuanAccount;
    }

    public void setFukuanAccount(String fukuanAccount) {
        this.fukuanAccount = fukuanAccount;
    }

    public String getShoukuanAccount() {
        return shoukuanAccount;
    }

    public void setShoukuanAccount(String shoukuanAccount) {
        this.shoukuanAccount = shoukuanAccount;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(@NonNull ZijinDiaoboBean o) {
        int i = this.getIndex() - o.getIndex();//先按照年龄排序
//        if(i == 0){
//            return this.score - o.getScore();//如果年龄相等了再用分数进行排序
//        }
        return i;
    }
}

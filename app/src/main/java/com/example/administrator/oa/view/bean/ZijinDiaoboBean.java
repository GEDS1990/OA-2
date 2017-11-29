package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ZijinDiaoboBean {
    String fukuanAccount;
    String shoukuanAccount;
    String money;

    public ZijinDiaoboBean(String fukuanAccount, String shoukuanAccount, String money) {
        this.fukuanAccount = fukuanAccount;
        this.shoukuanAccount = shoukuanAccount;
        this.money = money;
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
}

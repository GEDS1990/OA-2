package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ZijngDiaoboshenheBean {
    String pay;
    String income;
    String money;

    public ZijngDiaoboshenheBean(String pay, String income, String money) {
        this.pay = pay;
        this.income = income;
        this.money = money;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}

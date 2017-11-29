package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/8/1.
 */

public class HuiqianrenBean {
    String name;
    String id;
    String status;

    public HuiqianrenBean(String name, String id, String status) {
        this.name = name;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/28.
 */
public class ZuzhiUserBean {

    /**
     * id : 8
     * name : robot
     */

    private String id;
    private String name;

    public ZuzhiUserBean(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ZuzhiUserBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

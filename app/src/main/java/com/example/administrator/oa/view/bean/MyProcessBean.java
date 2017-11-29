package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/9/19.
 */

public class MyProcessBean {

    /**
     * code : 200
     * message : null
     * data : [{"processInstancesId":"1515001","name":"请假流程-职员1-2017-08-18","taskId":"779919726854144"},{"processInstancesId":"1515033","name":"请假流程-职员1-2017-08-18","taskId":"779928603803648"},{"processInstancesId":"1515142","name":"借款申请-职员1-2017-08-18","taskId":"780188622127104"},{"processInstancesId":"1515157","name":"出差申请-职员1-2017-08-18","taskId":"780189140942848"},{"processInstancesId":"1515176","name":"工作联系-职员1-2017-08-18","taskId":"780190157783040"},{"processInstancesId":"1515199","name":"工作联系-职员1-2017-08-18","taskId":"780190894653440"},{"processInstancesId":"1515222","name":"办文流程-职员1-2017-08-18","taskId":"780191463538688"},{"processInstancesId":"1515238","name":"用车申请-职员1-2017-08-18","taskId":"780191966232576"},{"processInstancesId":"1515257","name":"新闻发布-职员1-2017-08-18","taskId":"780192180486144"},{"processInstancesId":"1515299","name":"低值易耗品领用-职员1-2017-08-18","taskId":"780192719831040"}]
     */

    private int code;
    private Object message;
    private List<MyProcessInfoBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<MyProcessInfoBean> getData() {
        return data;
    }

    public void setData(List<MyProcessInfoBean> data) {
        this.data = data;
    }

}

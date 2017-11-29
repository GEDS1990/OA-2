package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class QingjiaShenheResponse {

    /**
     * code : 200
     * data : [{"label":"departments","name":"departments","readOnly":"Y","type":"textfield","value":"test"},{"label":"name","name":"name","readOnly":"Y","type":"textfield","value":"临远"},{"label":"startTime","name":"startTime","readOnly":"Y","type":"datepicker","value":"2017-04-13"},{"label":"endTime","name":"endTime","readOnly":"Y","type":"datepicker","value":"2017-04-13"},{"items":"按小时请假,按天请假","label":"day","name":"day","readOnly":"Y","type":"radio","value":""},{"label":"number","name":"number","readOnly":"Y","type":"textfield","value":"半天"},{"items":"事假,病假,婚假,其他","label":"type","name":"type","readOnly":"Y","type":"radio","value":"事假"},{"label":"reason","name":"reason","readOnly":"Y","type":"textarea","value":"test"},{"items":"同意,不同意","label":"comment","name":"comment","readOnly":"Y","type":"radio","value":"同意"}]
     */

    private int code;
    private List<QingjiaShenheBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<QingjiaShenheBean> getData() {
        return data;
    }

    public void setData(List<QingjiaShenheBean> data) {
        this.data = data;
    }

}

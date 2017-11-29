package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class ProcssDefinListResponse {

    /**
     * code : 200
     * message : null
     * data : [{"processDefinitionId":"leave:12:600333","name":"请假流程"},{"processDefinitionId":"publish:43:600321","name":"发文流程"},{"processDefinitionId":"assessment:12:600305","name":"企业考核"},{"processDefinitionId":"travel:17:600337","name":"出差申请"},{"processDefinitionId":"contact:20:567928","name":"工作联系"},{"processDefinitionId":"lease:6:600325","name":"企业退租"},{"processDefinitionId":"enter:11:600329","name":"企业入驻"},{"processDefinitionId":"article:10:600317","name":"办文流程"},{"processDefinitionId":"collect:14:567932","name":"收文流程"},{"processDefinitionId":"car:8:600313","name":"用车申请"},{"processDefinitionId":"news:8:600309","name":"新闻发布"},{"processDefinitionId":"protocol:8:600301","name":"投资协议流程"},{"processDefinitionId":"rental:7:600297","name":"租房流程"},{"processDefinitionId":"goods:7:635069","name":"低值易耗品领用"},{"processDefinitionId":"asset:8:635073","name":"固定资产领用"},{"processDefinitionId":"road:5:600285","name":"道路开挖占用流程"},{"processDefinitionId":"sealApply:5:720008","name":"用印申请"},{"processDefinitionId":"tender:3:567940","name":"招投标流程"},{"processDefinitionId":"loan:5:567956","name":"借款申请"},{"processDefinitionId":"money:5:600341","name":"报销流程"},{"processDefinitionId":"contract:2:715488","name":"合同流程"},{"processDefinitionId":"budget:2:752845","name":"预算合约部合同流程"},{"processDefinitionId":"secret:2:727888","name":"涉密合同流程"},{"processDefinitionId":"secret:3:915085","name":"涉密合同流程"},{"processDefinitionId":"capital:1:720156","name":"资金调拨"},{"processDefinitionId":"payment:1:720164","name":"资金申请（合同付款）"},{"processDefinitionId":"pay:1:720160","name":"资金申请（非合同）"},{"processDefinitionId":"urge:2:925041","name":"督办流程"}]
     */

    private int code;
    private Object message;
    private List<ProcssDefinBean> data;

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

    public List<ProcssDefinBean> getData() {
        return data;
    }

    public void setData(List<ProcssDefinBean> data) {
        this.data = data;
    }

}

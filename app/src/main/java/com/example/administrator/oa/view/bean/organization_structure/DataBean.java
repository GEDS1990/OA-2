package com.example.administrator.oa.view.bean.organization_structure;

import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */
public class DataBean {
    /**
     * children : [{"children":[{"children":[{"children":[{"id":621811703578624,"name":"资产运营部部长","open":false},{"id":621812880752640,"name":"信息填写员","open":false}],"id":621806243233792,"name":"资产运营部","open":true},{"id":618491152908288,"name":"企业服务部部长","open":false},{"id":630303118163968,"name":"公司领导","open":false},{"id":627492121886720,"name":"租房管理员","open":false},{"id":630221441433600,"name":"企业通知员","open":false}],"id":2,"name":"企业服务部","open":true,"ref":"1"},{"children":[{"id":596385168424960,"name":"综合管理部部长","open":false},{"id":7,"name":"OA系统维护","open":false,"ref":"7"},{"id":630253175422976,"name":"机要员","open":false},{"id":562246149816320,"name":"物业管理","open":false,"ref":""},{"id":628725163606016,"name":"物品管理员","open":false},{"id":656792115707904,"name":"合同备案专员","open":false}],"id":3,"name":"综合管理部","open":true,"ref":"2"}],"id":596302307540992,"name":"分管领导1","open":true,"ref":""},{"id":619955120357376,"name":"部门小组","open":false},{"children":[{"id":621790918230016,"name":"企业考核员","open":false},{"id":627243101798400,"name":"退租意见管理员","open":false},{"id":627405561675776,"name":"投资促进与人才工作部部长","open":false},{"id":630214174720000,"name":"投资协议管理员","open":false}],"id":557934866464768,"name":"投资促进与人才工作部","open":true,"ref":"557934865858560"},{"id":646855428554752,"name":"人事部","open":false},{"id":621807701688320,"name":"书记","open":false},{"children":[{"children":[{"id":611438081867776,"name":"合同专员","open":false},{"id":618726778454016,"name":"预算合约部部长","open":false},{"id":656791537991680,"name":"合同备案人","open":false}],"id":557934167752704,"name":"预算合约部","open":true,"ref":"557934167064576"},{"children":[{"id":596388263510016,"name":"工程建设部部长","open":false}],"id":557934632943616,"name":"工程建设部","open":true,"ref":"557934631829504"},{"children":[{"id":621790918230016,"name":"企业考核员","open":false},{"id":627243101798400,"name":"退租意见管理员","open":false},{"id":627405561675776,"name":"投资促进与人才工作部部长","open":false},{"id":630214174720000,"name":"投资协议管理员","open":false}],"id":557934866464768,"name":"投资促进与人才工作部","open":true,"ref":"557934865858560"}],"id":596303195226112,"name":"分管领导3","open":true,"ref":""},{"children":[{"children":[{"id":596385613561856,"name":"土地规划部部长","open":false}],"id":4,"name":"土地规划部","open":true,"ref":"3"},{"children":[{"id":596386231615488,"name":"财务金融部部长","open":false},{"id":596200236187648,"name":"投融资","open":false},{"id":596202071883776,"name":"主办会计","open":false},{"id":596203289477120,"name":"出纳","open":false},{"id":596204195774464,"name":"人事考勤","open":false},{"id":627471190605824,"name":"租房办理员","open":false},{"id":656793405374464,"name":"合同备案员","open":false}],"id":5,"name":"财务金融部","open":true,"ref":"4"}],"id":596302711013376,"name":"分管领导2","open":true,"ref":""},{"id":596203289477120,"name":"出纳","open":false},{"id":598673778769920,"name":"主任","open":false},{"id":646901730148352,"name":"ghbcgh","open":false}]
     * id : 1
     * name : 江北新区研创园
     * open : true
     * ref : 1
     */

    private int id;
    private String name;
    private boolean open;
    private String ref;
    private List<ChildrenBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

}

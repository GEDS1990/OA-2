package com.example.administrator.oa.view.bean.organization_structure;

import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */
public class ChildrenBean {
    /**
     * children : [{"children":[{"children":[{"id":621811703578624,"name":"资产运营部部长","open":false},{"id":621812880752640,"name":"信息填写员","open":false}],"id":621806243233792,"name":"资产运营部","open":true},{"id":618491152908288,"name":"企业服务部部长","open":false},{"id":630303118163968,"name":"公司领导","open":false},{"id":627492121886720,"name":"租房管理员","open":false},{"id":630221441433600,"name":"企业通知员","open":false}],"id":2,"name":"企业服务部","open":true,"ref":"1"},{"children":[{"id":596385168424960,"name":"综合管理部部长","open":false},{"id":7,"name":"OA系统维护","open":false,"ref":"7"},{"id":630253175422976,"name":"机要员","open":false},{"id":562246149816320,"name":"物业管理","open":false,"ref":""},{"id":628725163606016,"name":"物品管理员","open":false},{"id":656792115707904,"name":"合同备案专员","open":false}],"id":3,"name":"综合管理部","open":true,"ref":"2"}]
     * id : 596302307540992
     * name : 分管领导1
     * open : true
     * ref :
     */

    private long id;
    private String name;
    private boolean open;
    private String ref;
    private List<Object> temp;
    private List<ChildrenBean> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/4.
 */
public class LoginBean {
    /**
     * departmentId : 3
     * departmentName : 综合管理部
     * departments : 企业服务部,综合管理部,财务金融部,预算合约部,工程建设部,投资促进与人才工作部
     * sessionId : c6d10082-9d44-4bb3-8fc3-c0b63598b451
     * userId : 1
     * userName : 临远
     * userType : admin
     */

    private long departmentId;
    private String departmentName;
    private String departments;
    private String sessionId;
    private String userId;
    private String userName;
    private String userType;
    private String email;
    private String mobile;

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", departments='" + departments + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userType='" + userType + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}

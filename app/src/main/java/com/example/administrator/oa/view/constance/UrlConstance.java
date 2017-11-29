package com.example.administrator.oa.view.constance;

/**
 * Created by Administrator on 2017/6/27.
 */

public class UrlConstance {

    public static String formatUrl(String url) {

        if (url.startsWith("http")) {
            return url;
        } else {
            return URL_HOST + url;
        }
    }

    /**
     * 网络请求URL的公共部分
     */
//    public static final String URL_HOST = "http://192.168.1.150:8089/rs/android/";
    //public static final String URL_HOST = "http://192.168.173.1:8089/rs/android/";
    //public static final String URL_HOST = "http://58.213.75.45:8096/NJSmartOA/rs/android/";
    //public static final String URL_HOST = "http://192.168.10.125:8089/rs/android/";
    public static final String URL_HOST = "http://175av53846.iask.in/njsmartoa/rs/android/";
    //public static final String URL_HOST = "http://175av53846.iask.in/NJSmartOA/rs/android/";
    //public static final String URL_HOST = "http://175av53846.iask.in/NJSmartOA/rs/android/";
    //public static final String URL_HOST = "http://192.168.1.20:8080/NJSmartOA/rs/android/";

    /**
     * 上传文件
     */
    public static final String URL_UPLOAD = "http://175av53846.iask.in/njsmartoa/uploadFileController/uploadFile.do";

    /**
     * 登录
     */
    public static final String URL_LOGIN = formatUrl("login");

    /**
     * 获取消息列表
     */
    public static final String URL_MSG_LIST = formatUrl("msg/msg");

    /**
     * 获取单条消息详情
     */
    public static final String URL_MSG_DETAIL = formatUrl("msg/view");

    /**
     * 获取公告列表
     */
    public static final String URL_GONGGAO_LIST = formatUrl("cms/articles");

    /**
     * 获取单个公告详情
     */
    public static final String URL_GONGGAO_DETAIL = formatUrl("cms/articleInfo");

    /**
     * 流程定义接口
     */
    public static final String URL_PROCESS_DEFIN = formatUrl("bpm/processDefinitions");

    /**
     * 保存草稿接口
     */
    public static final String URL_SAVEDRAFT = formatUrl("bpm/saveDraft");

    /**
     * 发起请假流程
     */
    public static final String URL_STARTPROCESS = formatUrl("bpm/startProcess");

    /**
     * 表单页面生成编码接口
     */
    public static final String URL_BIAODAN_CODE = formatUrl("form/formCode");

    /**
     * 获取组织机构信息接口
     */
    public static final String URL_GET_ZUZHI = formatUrl("user/orgList");

    /**
     * 获取特定组织机构从属用户信息
     */
    public static final String URL_GET_ZUZHI_USERS = formatUrl("user/orgUserList");

    /**
     * 获取待办任务列表
     */
    public static final String URL_GET_DAIBAN_TASK_LIST = formatUrl("task/tasksPersonal");

    /**
     * 获取已办任务列表
     */
    public static final String URL_GET_YIBAN_TASK_LIST = formatUrl("task/tasksComplete");

    /**
     * 流程审核记录
     * task/logTask
     */
    public static final String URL_GET_PROCESS_HESTORY = formatUrl("task/logTask");

    /**
     * 具体流程节点表单初始化数据接口
     */
    public static final String URL_GET_PROCESS_INIT = formatUrl("form/viewTaskFormJson");

    /**
     * 提交某一项具体任务数据接口
     * task/completeTask
     */
    public static final String URL_PROCESS_COMMIT = formatUrl("task/completeTask");

    /**
     * 提交某一项具体任务数据接口
     * task/completeTask
     */
    public static final String URL_PROCESS_TASKTYPE = formatUrl("task/getTaskType");

    /**
     * 提交某一项具体任务数据接口
     * task/voteReturn
     */
    public static final String URL_HUIQIAN_TUIHUI = formatUrl("task/voteReturn");

    /**
     * 获取印章类型接口
     */
    public static final String URL_GET_YINGZHANG_TYPE = formatUrl("form/getStampList");

    /**
     * 未接流程
     * bpm/processInstancesRunning
     */
    public static final String URL_NOT_COMPLETE_PRO = formatUrl("bpm/processInstancesRunning");
    /**
     * 已结流程
     * /bpm/processInstancesComplete
     */
    public static final String URL_COMPLETE_PRO = formatUrl("bpm/processInstancesComplete");

    /**
     * 获取联系人列表
     */
    public static final String URL_CONTACTS_LIST = formatUrl("user/getContacts");

    /**
     * 草稿箱
     */
    public static final String URL_CAOGAOXIANG = formatUrl("bpm/processInstancesDraft");

    /**
     * 草稿箱详情
     */
    public static final String URL_CAOGAOXIANG_INFO = formatUrl("form/viewStartFormDraftJson");
}

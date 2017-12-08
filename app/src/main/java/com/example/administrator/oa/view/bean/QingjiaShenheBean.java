package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/26.
 */
public class QingjiaShenheBean {
    /**
     * label : departments
     * name : departments
     * readOnly : Y
     * type : textfield
     * value : test
     * items : 按小时请假,按天请假
     */
    private String formName;
    private String formCode;

    private String label;
    private String name;
    private String readOnly;
    private String type;
    private String value;
    private String items;
    private String size;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }
}

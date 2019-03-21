package com.template.dal.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.template.dal.base.BaseModel;

import java.io.Serializable;

public class User extends BaseModel {

    private String name;
    private Integer age;
    private Integer sex;
    private String address;

    @TableField(value="login_id")
    private String loginId;
    private String password;

    public User(String loginId) {
        this.loginId = loginId;
        this.setDelFlg(1);
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Serializable pkVal() {
        return getId();
    }
}

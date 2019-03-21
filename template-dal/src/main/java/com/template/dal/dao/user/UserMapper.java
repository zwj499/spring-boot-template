package com.template.dal.dao.user;

import com.template.dal.base.BaseMapper;
import com.template.dal.model.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<User> queryList();
}

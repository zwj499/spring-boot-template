package com.template.dal.dao.user;

import com.template.dal.base.BaseMapper;
import com.template.dal.model.User;

import java.util.List;

/**
 * @author zhuangwj
 * @since 2019/3/5
 */
public interface UserMapper extends BaseMapper<User> {
    List<User> queryList();
}

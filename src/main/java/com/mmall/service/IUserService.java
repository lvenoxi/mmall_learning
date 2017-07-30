package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Administrator on 2017/7/30.
 */
public interface IUserService {
    public ServerResponse<User> login(String username, String password);
}

package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Administrator on 2017/7/30.
 */
public interface IUserService {
    /**
     * 登录接口
     * @param username
     * @param password
     * @return
     */
    public ServerResponse<User> login(String username, String password);


    /**
     * 注册接口
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user);
}

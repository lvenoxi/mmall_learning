package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/7/30.
 */
public interface IUserService {
    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);


    /**
     * 注册接口
     *
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 检测用户名或者eamil是否存在
     *
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);


    /**
     * 忘记问题调用的接口
     *
     * @param username
     * @return
     */
    ServerResponse<String> forgetGetQuestion(String username);

    /**
     * 忘记问题时，校验问题的答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    /**
     * 忘记密码重置密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> fogetRestPassword(String username, String passwordNew, String forgetToken);

    /**
     * 登录状态下重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServerResponse<String> restPassword(String passwordOld, String passwordNew, User user);

    /**
     * 登录状态下更新用户信息
     *
     * @param user
     * @return
     */
    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);
}



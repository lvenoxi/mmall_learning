package com.mmall.service.imple;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.util.MD5Util;

/**
 * Created by Administrator on 2017/7/30.
 */
@Service("iUserService")
public class UserServiceImple implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录service
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int checkNum = userMapper.checkUsername(username);
        if (checkNum==0){
            //如果没有该用户，则提示用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User  user = userMapper.selectLogin(username, password);
        if (user==null){
            //如果用户名和密码不匹配，则提示密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //密码设为空，返回数据给前台
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 注册操作
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
        int checkNum = userMapper.checkUsername(user.getUsername());
        if (checkNum>0){
            //如果用户名已经存在，则提示用户名已存在
            return ServerResponse.createByErrorMessage("用户已存在");
        }

        checkNum = userMapper.checkEmail(user.getEmail());
        if (checkNum>0){
            //如果email已存在，则提示邮箱已注册
            return ServerResponse.createByErrorMessage("Email已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword( MD5Util.MD5EncodeUtf8(user.getPassword()));




        return null;
    }


}

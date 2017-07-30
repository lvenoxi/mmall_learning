package com.mmall.service.imple;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        //todo 将密码进行MD5,到数据库中查询
        //String md5Password = MD5Uitl.MD5EncodeUtf8(password);
        User  user = userMapper.selectLogin(username, password);
        if (user==null){
            //如果用户名和密码不匹配，则提示密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //密码设为空，返回数据给前台
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}

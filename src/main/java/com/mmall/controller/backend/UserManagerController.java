package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/8/12.
 */
@Controller
@RequestMapping("/manager/user")
public class UserManagerController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String userName, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(userName,password);
        if (response.isSuccess()){
            //判断是否为管理员角色
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                //为管理员角色
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }else {
                //非管理员角色
                return ServerResponse.createByErrorMessage("用户不是管理员");
            }
        }

        return response;
    }
}

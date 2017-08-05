package com.mmall.controller.portable;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.SavepointManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/7/30.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 登录操作
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping( value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){

        //service->mybatis->dao;
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return  response;
    }


    /**
     * 退出登录操作
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    /**
     * 注册操作
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }


    /**
     * 校验用户名或者邮箱的接口
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }


    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        return ServerResponse.createBySuccess(user);
    }


    /**
     * 忘记密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.forgetCheckAnswer(username,question,answer);
    }

    @RequestMapping(value = "foget_rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> fogetRestPassword(String username, String passwordNew, String forgetToken){
        return iUserService.fogetRestPassword(username, passwordNew, forgetToken);
    }


    /**
     * 登录状态下更新信息
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "rest_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> restPassword(HttpSession session, String passwordOld, String passwordNew){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user ==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iUserService.restPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user){
        User currenUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currenUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        user.setId(currenUser.getId());
        user.setUsername(currenUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getData().setUsername(currenUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }

        return response;
    }


    public ServerResponse<User> getInformation(HttpSession session){
        // TODO: 2017/8/5  
    }
}

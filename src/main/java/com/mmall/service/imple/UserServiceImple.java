package com.mmall.service.imple;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.util.MD5Util;

import java.util.UUID;

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
        User  user = userMapper.selectLogin(username, md5Password);
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
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }

        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if ( !validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword( MD5Util.MD5EncodeUtf8(user.getPassword()));

        int checkNum = userMapper.insert(user);
        if (checkNum == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }


        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if ( StringUtils.isNotBlank(type) ){
            //开始校验
            if (Const.USERNAME.equals( type )){
                //判断用户名
                int checkNum = userMapper.checkUsername(str);
                if (checkNum>0){
                    //如果用户名已经存在，则提示用户名已存在
                    return ServerResponse.createByErrorMessage("用户已存在");
                }

            }
            if (Const.EMAIL.equals( type )){
                //判断邮箱
                int checkNum = userMapper.checkEmail(str);
                if (checkNum>0){
                    //如果email已存在，则提示邮箱已注册
                    return ServerResponse.createByErrorMessage("Email已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 忘记问题调用的接口
     *
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if ( !validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if ( StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }


    /**
     * 忘记问题时，校验问题的答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int reultCount = userMapper.checkAnswer(username, question, answer);
        if (reultCount<=0){
            //用户、问题、答案不对
            return ServerResponse.createByErrorMessage("问题答案不对");
        }

        String fogetToken = UUID.randomUUID().toString();
        TokenCache.setKey("token_"+username, fogetToken);

        return ServerResponse.createBySuccess(fogetToken);
    }

    @Override
    public ServerResponse<String> fogetRestPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }

        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getByKey(TokenCache.TOKEN_PREFIX+username);
        if ( StringUtils.isBlank(token)){
            //如果获取的token是空的
            return ServerResponse.createByErrorMessage("token无效获取过期");
        }

        if (StringUtils.equals(token, forgetToken)){
            //token一致
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录状态下重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> restPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查阅一个count(1)，如果不指定id,那么结果就是true
        String md5Password = MD5Util.MD5EncodeUtf8(passwordOld);
        int resultCount = userMapper.checkPassword(md5Password, user.getId());
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 登录状态下更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话，不能是我们当前的这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("email已经被使用，无法更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }

        return ServerResponse.createByErrorMessage("更新失败");
    }


}

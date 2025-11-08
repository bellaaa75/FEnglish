package org.example.fenglish.service;

import org.example.fenglish.entity.Administrator;
import org.example.fenglish.entity.OrdinaryUser;


import java.util.Map;


public interface UserService {

    // 普通用户注册
    Map<String, Object> registerOrdinaryUser(OrdinaryUser user);

    // 管理员注册
    Map<String, Object> registerAdministrator(Administrator admin);

    // 用户登录
    Map<String, Object> login(String identifier, String password, String userType);


    // 获取用户信息
    Map<String, Object> getUserInfo(String userID);

    // 修改用户信息
    Map<String, Object> updateUserInfo(String userID, Map<String, String> userInfo);

    // 修改密码
    Map<String, Object> changePassword(String userID, String oldPassword, String newPassword);

    // 注销账户
    Map<String, Object> deleteAccount(String userID, String password);
}

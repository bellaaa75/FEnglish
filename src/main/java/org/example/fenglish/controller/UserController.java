package org.example.fenglish.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.fenglish.entity.Administrator;
import org.example.fenglish.entity.OrdinaryUser;
import org.example.fenglish.service.UserService;
import org.example.fenglish.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    // 普通用户注册
    @PostMapping("/ordinary/register")
    public Map<String, Object> registerOrdinaryUser(@RequestBody OrdinaryUser user) {
        return userService.registerOrdinaryUser(user);
    }

    // 管理员注册
    @PostMapping("/admin/register")
    public Map<String, Object> registerAdministrator(@RequestBody Administrator admin) {
        return userService.registerAdministrator(admin);
    }

    // 普通用户登录
    @PostMapping("/ordinary/login")
    public Map<String, Object> loginOrdinaryUser(@RequestBody Map<String, String> loginRequest) {
        String identifier = loginRequest.get("identifier");
        String password = loginRequest.get("password");
        return userService.login(identifier, password, "ORDINARY");
    }

    // 管理员登录
    @PostMapping("/admin/login")
    public Map<String, Object> loginAdministrator(@RequestBody Map<String, String> loginRequest) {
        String identifier = loginRequest.get("identifier");
        String password = loginRequest.get("password");
        return userService.login(identifier, password, "ADMIN");
    }

    // 获取用户信息
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestAttribute String userID) {
        System.out.println("从拦截器获取userID: " + userID);
        return userService.getUserInfo(userID);
    }

    // 修改用户信息（仅普通用户）
    @PutMapping("/info")
    public Map<String, Object> updateUserInfo(@RequestBody Map<String, String> userInfo,
                                              @RequestAttribute String userID) {
        return userService.updateUserInfo(userID, userInfo);
    }

    // 修改密码
    @PutMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> passwordRequest,
                                              @RequestAttribute String userID) {
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        return userService.changePassword(userID, oldPassword, newPassword);
    }

    // 注销账户
    @DeleteMapping("/account")
    public Map<String, Object> deleteAccount(@RequestBody Map<String, String> deleteRequest,
                                             @RequestAttribute String userID) {
            String password = deleteRequest.get("password");
            return userService.deleteAccount(userID, password);
    }

}

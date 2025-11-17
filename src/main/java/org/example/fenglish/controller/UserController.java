package org.example.fenglish.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.fenglish.entity.Administrator;
import org.example.fenglish.entity.OrdinaryUser;
import org.example.fenglish.service.UserService;
import org.example.fenglish.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    // 普通用户注册
    @PostMapping("/user/register")
    public Map<String, Object> registerOrdinaryUser(@RequestBody OrdinaryUser user) {
        return userService.registerOrdinaryUser(user);
    }

    // 管理员注册
    @PostMapping("/admin/register")
    public Map<String, Object> registerAdministrator(@RequestBody Administrator admin) {
        return userService.registerAdministrator(admin);
    }

    // 普通用户登录
    @PostMapping("/user/login")
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
    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo(@RequestAttribute String userID) {
        System.out.println("从拦截器获取userID: " + userID);
        return userService.getUserInfo(userID);
    }

    // 修改用户信息（仅普通用户）
    @PutMapping("/user/info")
    public Map<String, Object> updateUserInfo(@RequestBody Map<String, String> userInfo,
                                              @RequestAttribute String userID) {
        return userService.updateUserInfo(userID, userInfo);
    }

    // 修改密码
    @PutMapping("/user/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> passwordRequest,
                                              @RequestAttribute String userID) {
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        return userService.changePassword(userID, oldPassword, newPassword);
    }

    // 注销账户
    @DeleteMapping("/user/account")
    public Map<String, Object> deleteAccount(@RequestBody Map<String, String> deleteRequest,
                                             @RequestAttribute String userID) {
            String password = deleteRequest.get("password");
            return userService.deleteAccount(userID, password);
    }

    // 管理员分页查询所有普通用户（仅管理员可访问）
    @GetMapping("/admin/list") // 路径加 /admin 区分，清晰明了
    /*@PreAuthorize("hasRole('ADMIN')")*/ // 仅管理员角色可访问
    public ResponseEntity<Map<String, Object>> getOrdinaryUserList(
            @RequestParam(required = false) String keyword, // 可选搜索关键词
            @RequestParam(defaultValue = "0") int page, // 默认第1页（页码从0开始）
            @RequestParam(defaultValue = "4") int size) { // 默认每页4条

        // 构建分页参数（按用户ID升序排序）
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "userId"));
        // 调用UserService中已实现的分页查询方法
        Page<OrdinaryUser> userPage = userService.getOrdinaryUsers(keyword, pageable);

        // 封装分页结果（给前端友好的响应格式）
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("users", userPage.getContent()); // 当前页普通用户数据
        result.put("totalPages", userPage.getTotalPages()); // 总页数
        result.put("totalCount", userPage.getTotalElements()); // 总用户数
        result.put("currentPage", page); // 当前页码

        return ResponseEntity.ok(result);
    }

    // 管理员查询特定普通用户（仅管理员可访问）
    @GetMapping("/admin/{userId}")
    /*@PreAuthorize("hasRole('ADMIN')")*/
    public ResponseEntity<Map<String, Object>> getOrdinaryUserByAdmin(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        result = userService.getOrdinaryUserByUserId(userId);      
        return ResponseEntity.ok(result);
    }
}

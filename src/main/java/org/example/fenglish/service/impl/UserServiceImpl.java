package org.example.fenglish.service.impl;

import jakarta.transaction.Transactional;
import org.example.fenglish.entity.Administrator;
import org.example.fenglish.entity.OrdinaryUser;
import org.example.fenglish.entity.User;
import org.example.fenglish.repository.AdministratorRepository;
import org.example.fenglish.repository.OrdinaryUserRepository;
import org.example.fenglish.repository.UserRepository;
import org.example.fenglish.service.UserService;
import org.example.fenglish.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private OrdinaryUserRepository ordinaryUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //普通用户注册
    @Override
    public Map<String, Object> registerOrdinaryUser(OrdinaryUser user) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(user.getUserMailbox())) {
                result.put("success", false);
                result.put("message", "邮箱已被注册");
                return result;
            }

            // 检查手机号是否已存在
            if (userRepository.existsByPhone(user.getPhoneNumber())) {
                result.put("success", false);
                result.put("message", "手机号已被注册");
                return result;
            }

            // 生成用户ID
            String userId = "OU_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            OrdinaryUser newUser = new OrdinaryUser();
            newUser.setUserId(userId);
            newUser.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
            newUser.setUserType("ORDINARY");
            newUser.setUserName(user.getUserName());
            newUser.setRegisterTime(LocalDateTime.now());
            newUser.setPhoneNumber(user.getPhoneNumber());
            newUser.setUserMailbox(user.getUserMailbox());
            newUser.setGender(user.getGender());


            ordinaryUserRepository.save(newUser);


            result.put("success", true);
            result.put("message", "注册成功");
            result.put("userId", userId);


        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册失败: " + e.getMessage());
        }

        return result;
    }

    //管理员注册
    @Override
    public Map<String, Object> registerAdministrator(Administrator admin) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成管理员ID
            String adminID = "AD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            Administrator newAdmin = new Administrator();
            newAdmin.setUserId(adminID);

            // 加密密码
            newAdmin.setUserPassword(passwordEncoder.encode(admin.getUserPassword()));
            newAdmin.setUserType("ADMIN");

            // 保存管理员
            /*User baseUser = new User();
            baseUser.setUserId(adminID);
            baseUser.setUserPassword(passwordEncoder.encode(admin.getUserPassword())); // 与子表密码一致
            baseUser.setUserType("ADMIN");
            userRepository.save(baseUser); //保存到基表*/

            administratorRepository.save(newAdmin);

            result.put("success", true);
            result.put("message", "管理员注册成功");
            result.put("userId", adminID);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "管理员注册失败: " + e.getMessage());
        }

        return result;
    }

    //登录
    @Override
    public Map<String, Object> login(String identifier, String password, String userType) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<User> userOptional;

            if ("ORDINARY".equals(userType)) { // 普通用户登录
                // 通过邮箱或手机号查找用户
                userOptional = userRepository.findByEmailOrPhone(identifier);
                // 如果普通用户存在，通过 userID 查 User 表（获取密码用于验证）
            } else { // 管理员登录
                // 管理员直接使用userID登录
                userOptional = userRepository.findById(identifier);
            }

            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            User user = userOptional.get();
            // 检查用户类型是否匹配
            if (!userType.equals(user.getUserType())) {
                result.put("success", false);
                result.put("message", "用户类型不匹配");
                return result;
            }

            // 验证密码
            if (!passwordEncoder.matches(password, user.getUserPassword())) {
                result.put("success", false);
                result.put("message", "密码错误");
                return result;
            }

            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), userType);

            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("userId", user.getUserId());
            result.put("userType", userType);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登录失败: " + e.getMessage());
        }

        return result;
    }

    // 获取用户信息
    @Override
    public Map<String, Object> getUserInfo(String userID) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<String> userTypeOptional = userRepository.findUserTypeById(userID);

            if (userTypeOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            String userType = userTypeOptional.get();

            if ("ORDINARY".equals(userType)) { // 普通用户
                Optional<OrdinaryUser> userOptional = ordinaryUserRepository.findById(userID);
                if (userOptional.isPresent()) {
                    OrdinaryUser user = userOptional.get();
                    result.put("success", true);
                    result.put("userInfo", user);
                } else {
                    result.put("success", false);
                    result.put("message", "用户信息不存在");
                }
            } else { // 管理员
                Optional<Administrator> adminOptional = administratorRepository.findById(userID);
                if (adminOptional.isPresent()) {
                    Administrator admin = adminOptional.get();
                    result.put("success", true);
                    result.put("userInfo", admin);
                } else {
                    result.put("success", false);
                    result.put("message", "管理员信息不存在");
                }
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败: " + e.getMessage());
        }

        return result;
    }

    // 修改用户信息
    @Override
    public Map<String, Object> updateUserInfo(String userID, Map<String, String> userInfo) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<String> userTypeOptional = userRepository.findUserTypeById(userID);

            if (userTypeOptional.isEmpty() || !"ORDINARY".equals(userTypeOptional.get())) {
                result.put("success", false);
                result.put("message", "只有普通用户可以修改个人信息");
                return result;
            }

            Optional<OrdinaryUser> userOptional = ordinaryUserRepository.findById(userID);
            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            OrdinaryUser user = userOptional.get();

            // 更新用户信息
            if (userInfo.containsKey("userName")) {
                user.setUserName(userInfo.get("userName"));
            }

            if (userInfo.containsKey("gender")) {
                user.setGender(userInfo.get("gender"));
            }

            if (userInfo.containsKey("phoneNumber")) {
                String newPhone = userInfo.get("phoneNumber");
                if(newPhone == null || newPhone.isEmpty()) {
                    
                }else if (!newPhone.equals(user.getPhoneNumber()) &&
                        ordinaryUserRepository.existsByPhoneExcludingUser(newPhone, userID)) {
                    result.put("success", false);
                    result.put("message", "手机号已被使用");
                    return result;
                }
                user.setPhoneNumber(newPhone);
            }

            if (userInfo.containsKey("userMailbox")) {
                String newEmail = userInfo.get("userMailbox");
                if (!newEmail.equals(user.getUserMailbox()) &&
                        ordinaryUserRepository.existsByEmailExcludingUser(newEmail, userID)) {
                    result.put("success", false);
                    result.put("message", "邮箱已被使用");
                    return result;
                }
                user.setUserMailbox(newEmail);
            }

            ordinaryUserRepository.save(user);

            result.put("success", true);
            result.put("message", "个人信息更新成功");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新个人信息失败: " + e.getMessage());
        }

        return result;
    }

    // 修改密码
    @Override
    public Map<String, Object> changePassword(String userID, String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<User> userOptional = userRepository.findById(userID);

            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            User user = userOptional.get();

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getUserPassword())) {
                result.put("success", false);
                result.put("message", "原密码错误");
                return result;
            }

            // 更新密码
            String encodedNewPwd = passwordEncoder.encode(newPassword);

            user.setUserPassword(encodedNewPwd);
            userRepository.save(user);

            String userType = user.getUserType();
            if ("ORDINARY".equals(userType)) {
                Optional<OrdinaryUser> ordinaryUserOpt = ordinaryUserRepository.findById(userID);
                if (ordinaryUserOpt.isEmpty()) {
                    throw new RuntimeException("普通用户子表记录不存在，无法同步密码");
                }
                OrdinaryUser ordinaryUser = ordinaryUserOpt.get();
                ordinaryUser.setUserPassword(encodedNewPwd); // 同步新密码
                ordinaryUserRepository.save(ordinaryUser);

            } else if ("ADMIN".equals(userType)) {
                Optional<Administrator> adminOpt = administratorRepository.findById(userID);
                if (adminOpt.isEmpty()) {
                    throw new RuntimeException("管理员子表记录不存在，无法同步密码");
                }
                Administrator administrator = adminOpt.get();
                administrator.setUserPassword(encodedNewPwd); // 同步新密码
                administratorRepository.save(administrator);
            }
            result.put("success", true);
            result.put("message", "密码修改成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "密码修改失败: " + e.getMessage());
        }

        return result;
    }

    // 注销账户
    @Override
    @Transactional
    public Map<String, Object> deleteAccount(String userID, String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<User> userOptional = userRepository.findById(userID);

            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            User user = userOptional.get();

            // 验证密码
            if (!passwordEncoder.matches(password, user.getUserPassword())) {
                result.put("success", false);
                result.put("message", "密码错误");
                return result;
            }

            // 根据用户类型删除相应的记录
            /*if ("ORDINARY".equals(user.getUserType())) {
                ordinaryUserRepository.deleteById(userID);
            } else {
                administratorRepository.deleteById(userID);
            }*/

            // 删除基表记录
            userRepository.deleteById(userID);

            result.put("success", true);
            result.put("message", "账户注销成功");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "账户注销失败: " + e.getMessage());
        }

        return result;
    }

    // 管理员分页查询普通用户（支持关键词搜索）
    @Override
    public Page<OrdinaryUser> getOrdinaryUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ordinaryUserRepository.findAll(pageable);
        }
        // 自定义SQL：按用户名或用户ID模糊查询
        return ordinaryUserRepository.findByKeyword(keyword, pageable);
    }

    // 管理员查询特定普通用户
    @Override
    public Map<String, Object> getOrdinaryUserByUserId(String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            OrdinaryUser user = ordinaryUserRepository.findByUserId(userId);
            if (user != null) {
                result.put("success", true);
                result.put("data", user);
                result.put("message", "查询成功");
            } else {
                result.put("success", false);
                result.put("data", null);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("data", null);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    // 管理员删除普通用户
    @Override
    public Map<String, Object> deleteOrdinaryUser(String userId){
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 校验用户是否存在
            if (!ordinaryUserRepository.existsByUserId(userId)) {
                result.put("success", false);
                result.put("message", "用户不存在，删除失败");
                return result;
            }

            // 2. 执行删除（按 userId 删除）
            userRepository.deleteById(userId);

            // 3. 返回成功结果
            result.put("success", true);
            result.put("message", "用户删除成功");
        } catch (Exception e) {
            // 4. 异常处理
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
    


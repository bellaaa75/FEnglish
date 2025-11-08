package org.example.fenglish.repository;

import org.example.fenglish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // 根据用户ID查找用户
    User findByUserId(String userId);

    // 检查用户ID是否存在
    boolean existsByUserId(String userId);

    //验证用户名与密码是否正确
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.userPassword = :password")
    User findByUserIdAndPassword(@Param("userId") String userId,
                                           @Param("password") String password);

    // 根据邮箱或手机号查找用户
    @Query("SELECT u FROM User u WHERE u.userId IN " +
            "(SELECT ou.userId FROM OrdinaryUser ou WHERE ou.userMailbox = :identifier OR ou.phoneNumber = :identifier)")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);

    // 检查邮箱是否存在
    @Query("SELECT COUNT(ou) > 0 FROM OrdinaryUser ou WHERE ou.userMailbox = :email")
    boolean existsByEmail(@Param("email") String email);

    // 检查手机号是否存在
    @Query("SELECT COUNT(ou) > 0 FROM OrdinaryUser ou WHERE ou.phoneNumber = :phone")
    boolean existsByPhone(@Param("phone") String phone);

    // 根据用户ID查找用户类型
    @Query("SELECT u.userType FROM User u WHERE u.userId = :userId")
    Optional<String> findUserTypeById(@Param("userId") String userId);
}

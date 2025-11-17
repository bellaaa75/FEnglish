package org.example.fenglish.repository;


import org.example.fenglish.entity.OrdinaryUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdinaryUserRepository extends JpaRepository<OrdinaryUser, String> {
    // 根据邮箱查找普通用户
    Optional<OrdinaryUser> findByUserMailbox(String userMailbox);

    // 根据手机号查找普通用户
    Optional<OrdinaryUser> findByPhoneNumber(String phoneNumber);

    // 检查用户ID是否存在
    boolean existsByUserId(String userId);

    // 检查邮箱是否存在（排除当前用户）
    @Query("SELECT COUNT(ou) > 0 FROM OrdinaryUser ou WHERE ou.userMailbox = :email AND ou.userId != :userId")
    boolean existsByEmailExcludingUser(@Param("email") String email, @Param("userId") String userId);

    // 检查手机号是否存在（排除当前用户）
    @Query("SELECT COUNT(ou) > 0 FROM OrdinaryUser ou WHERE ou.phoneNumber = :phone AND ou.userId != :userId")
    boolean existsByPhoneExcludingUser(@Param("phone") String phone, @Param("userId") String userId);

    // 分页+条件查询（用户名或用户ID模糊匹配）
    @Query("SELECT u FROM OrdinaryUser u WHERE u.userName LIKE %:keyword% OR u.userId LIKE %:keyword%")
    Page<OrdinaryUser> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 根据用户ID查询
    OrdinaryUser findByUserId(String userId);
}

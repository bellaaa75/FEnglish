package org.example.fenglish.repository;

import org.example.fenglish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository {
    // 根据用户ID查找用户
    User findByUserId(String userId);

    // 检查用户ID是否存在
    boolean existsByUserId(String userId);
}

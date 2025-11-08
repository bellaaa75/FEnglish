package org.example.fenglish.repository;

import org.example.fenglish.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository  extends JpaRepository<Administrator, String>{
    Administrator findByUserId(String userId);//查找管理员
    boolean existsByUserId(String userId);//判断管理员是否存在
}

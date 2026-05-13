package com.example.demo.mapper;

import com.example.demo.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/29 17:05
 */
@Repository
public interface UserRepository extends JpaRepository<SysUser,Long> {
    SysUser findByUserName(String userName);
}

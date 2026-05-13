package com.example.demo.service;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/29 17:10
 */
@Service
public class JdbcUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userRepository.findByUserName(username);
        return new User(sysUser.getUserName(), sysUser.getPassword(), new ArrayList<>());
    }
}

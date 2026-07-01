package com.example.demo.config;

import com.example.demo.service.JdbcUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/29 17:15
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    PasswordEncoder passwordEncoder;

    @Autowired
    JdbcUserDetailService jdbcUserDetailService;

    /**
     * 注册一个认证管理器对象到容器
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 数据库涉及到的加密都被我手动加密了，用这个东西。main方法
     * OAuth2 与 Bcrypt：官方标配
     * OAuth2 框架本身是关于授权流程的协议，它并不限制你使用哪种密码编码方式。
     * Spring Security 对 Bcrypt 提供了原生支持。
     *
     * 核心支持类：在 Spring Security 中，处理 Bcrypt 加密和校验的核心类是
     * BCryptPasswordEncoder。你之前看到的 ClientSecretAuthenticationProvider
     * 在执行校验时，正是通过这个类（或你配置的其他 PasswordEncoder）来比对明文和密文的。
     *
     * 标准做法：在配置 OAuth2 授权服务器时，通常会像下面的代码示例一样，将 BCryptPasswordEncoder
     * 声明为一个 Bean，并注入到 Spring Security 的配置中。这样，
     * 所有涉及密码校验的地方（包括 client_secret 和用户密码）都会使用 Bcrypt 算法。
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 处理用户名和密码验证事宜
     * 1）客户端传递username和password参数到认证服务器
     * 2）一般来说，username和password会存储在数据库中的用户表中
     * 3）根据用户表中数据，验证当前传递过来的用户信息的合法性
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jdbcUserDetailService).passwordEncoder(passwordEncoder);
        auth.userDetailsService(jdbcUserDetailService).passwordEncoder(passwordEncoder());
    }

}


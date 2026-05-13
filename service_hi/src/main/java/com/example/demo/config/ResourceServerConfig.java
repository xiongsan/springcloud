package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/29 17:23
 */
@Configuration
@EnableResourceServer
@EnableWebSecurity
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private String signingKey = "cloud123cloud123cloud123cloud123";

    @Value("${spring.application.name}")
    private String applicationName;

    public JwtAccessTokenConverter jwtAccessTokenConverter () {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(signingKey);
        jwtAccessTokenConverter.setVerifier(new MacSigner(signingKey));
        return jwtAccessTokenConverter;
    }

    public TokenStore tokenStore (){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 该方法用于定义资源服务器向远程认证服务器发起请求，进行token校验等事宜
     * @param resources
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(applicationName).tokenStore(tokenStore()).stateless(true);
    }

    /**
     * 场景：一个服务中可能有很多资源（API接口）
     *    某一些API接口，需要先认证，才能访问
     *    某一些API接口，压根就不需要认证，本来就是对外开放的接口
     *    我们就需要对不同特点的接口区分对待（在当前configure方法中完成），设置是否需要经过认证
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)// 设置session的创建策略（根据需要创建即可）
                .and()
                .authorizeRequests()
                .antMatchers("/hi/**").authenticated() // hi为前缀的请求需要认证
                .antMatchers("/user/**").authenticated() // user为前缀的请求需要认证
                .anyRequest().permitAll(); //  其他请求不认证
    }
}



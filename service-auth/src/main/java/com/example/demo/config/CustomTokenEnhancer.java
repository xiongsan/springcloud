package com.example.demo.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2026/6/24 14:38
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {
        // 从认证信息中获取用户详情
        UserDetails user = (UserDetails) authentication.getPrincipal();
        // 查询数据库获取用户角色、组织等信息
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("roles", user.getAuthorities());
        additionalInfo.put("organization", "某组织");
        additionalInfo.put("userId", "用户ID");

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
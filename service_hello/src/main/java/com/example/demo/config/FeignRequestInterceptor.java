package com.example.demo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author : HARRY
 * Description:
 * 微服务之间feign调用请求头丢失的问题
 * @Date : created in 2025/10/11 9:38
 */

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (httpServletRequest != null) {
            template.header("Authorization", httpServletRequest.getHeader("Authorization"));
            // 也可以传递所有请求头,防止部分丢失
//            Map<String, String> headers = getHeaders(httpServletRequest);
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                template.header(entry.getKey(), entry.getValue());
//            }
            log.debug("FeignRequestInterceptor:{}", template.toString());
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取原请求头
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }
}

package com.example.demo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/10 17:23
 */
@Component
@Order(value = Integer.MIN_VALUE)
public class AccessLogGlobalFilter implements GlobalFilter {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String AUTHORIZE_TOKEN = "token";
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();
        HttpHeaders headers = request.getHeaders();
        //从请求头中获取token
        String token = headers.getFirst(AUTHORIZE_TOKEN);
        if (token == null) {
            //从请求头参数中获取token
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        ServerHttpResponse response = exchange.getResponse();
        //如果token为空，直接返回401，未授权
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //处理完成，直接拦截，不再进行下去
            log.error("请求路径:{},响应码:{}", path, HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", "401");
                    map.put("message", "UNAUTHORIZED");
                    map.put("data", null);
                    return bufferFactory.wrap(objectMapper.writeValueAsBytes(map));
                }
                catch (JsonProcessingException e) {
                    log.error("Error writing response", e);
                    return bufferFactory.wrap(new byte[0]);
                }
            }));
        }
        //授权正常，继续下一个过滤器链的调用
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    HttpStatus statusCode = response.getStatusCode();
                    log.info("请求路径:{},响应码:{}", path, statusCode);
                }));

    }
}

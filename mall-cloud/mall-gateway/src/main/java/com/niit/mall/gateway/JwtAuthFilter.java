package com.niit.mall.gateway;

import com.niit.mall.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 网关统一鉴权：校验 JWT，把 userId/username/role 写入请求头传给下游。
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    /** 白名单：无需登录即可访问 */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/portal/login", "/api/portal/register",
            "/api/admin/login", "/api/service/login",
            "/ws/**", "/upload/**", "/druid/**",
            "/doc.html", "/swagger-ui.html", "/swagger-ui/**", "/v2/api-docs",
            "/swagger-resources/**", "/webjars/**", "/favicon.ico"
    );

    /** 白名单：仅 GET 公开 */
    private static final List<String> WHITE_GET = Arrays.asList(
            "/api/portal/home", "/api/portal/category/list",
            "/api/portal/product/**", "/api/portal/comment/list", "/api/portal/seckill/list"
    );

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // 白名单直接放行
        if (isWhitelisted(path, method)) {
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (token == null) {
            return unauthorized(exchange);
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long id = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            // 传递用户信息给下游服务
            ServerHttpRequest mutated = request.mutate()
                    .header("X-User-Id", String.valueOf(id))
                    .header("X-Username", username == null ? "" : username)
                    .header("X-User-Role", role == null ? "" : role)
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private boolean isWhitelisted(String path, HttpMethod method) {
        for (String p : WHITE_LIST) {
            if (matcher.match(p, path)) {
                return true;
            }
        }
        if (method == HttpMethod.GET) {
            for (String p : WHITE_GET) {
                if (matcher.match(p, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}";
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 在路由之前执行
        return -100;
    }
}

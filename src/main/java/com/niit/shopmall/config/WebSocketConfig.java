package com.niit.shopmall.config;

import com.niit.shopmall.security.JwtUtil;
import com.niit.shopmall.security.LoginUser;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 配置：客服聊天实时推送（STOMP over SockJS）
 *
 * 架构约定：消息「写」走 REST 接口（受 Spring Security 保护），
 * 本 WebSocket 仅作为「读」的实时推送通道，前端订阅 /topic/conversation/{id}。
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(handshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 服务端推送前缀
        registry.enableSimpleBroker("/topic");
        // 客户端发送前缀（预留）
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 握手拦截器：从 query 参数 token 校验 JWT，解析出当前用户。
     */
    private HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    String token = servletRequest.getServletRequest().getParameter("token");
                    if (token == null || token.isEmpty()) {
                        return false;
                    }
                    try {
                        Claims claims = jwtUtil.parseToken(token);
                        Long id = Long.valueOf(claims.getSubject());
                        String username = claims.get("username", String.class);
                        String role = claims.get("role", String.class);
                        attributes.put("user", new LoginUser(id, username, role));
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }
}

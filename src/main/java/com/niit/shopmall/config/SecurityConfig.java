package com.niit.shopmall.config;

import com.niit.shopmall.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // 前台公共接口
                .antMatchers("/api/portal/login", "/api/portal/register").permitAll()
                .antMatchers(HttpMethod.GET,
                        "/api/portal/home",
                        "/api/portal/category/list",
                        "/api/portal/product/**",
                        "/api/portal/comment/list",
                        "/api/portal/seckill/list").permitAll()
                // 后台登录
                .antMatchers("/api/admin/login").permitAll()
                // 客服端登录
                .antMatchers("/api/service/login").permitAll()
                // 接口文档与静态资源
                .antMatchers("/doc.html", "/swagger-ui.html", "/swagger-ui/**", "/v2/api-docs",
                        "/swagger-resources/**", "/webjars/**", "/favicon.ico").permitAll()
                .antMatchers("/upload/**").permitAll()
                .antMatchers("/druid/**").permitAll()
                // WebSocket 握手端点（token 在握手拦截器中校验）
                .antMatchers("/ws/**").permitAll()
                // 后台接口需要管理员
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                // 客服端接口需要客服
                .antMatchers("/api/service/**").hasRole("SERVICE")
                // 其余前台接口需要登录（会员）
                .antMatchers("/api/portal/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, e) ->
                        writeJson(response, 401, "未登录或登录已过期"))
                .accessDeniedHandler((request, response, e) ->
                        writeJson(response, 403, "无权限访问"))
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void writeJson(HttpServletResponse response, int code, String message) throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}

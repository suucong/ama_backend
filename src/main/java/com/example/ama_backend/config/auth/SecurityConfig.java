package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.JWTRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity //스프링 시큐리티 설정들을 활성화시킴
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// CORS 활성화
        http
            .cors()
            .and()
            .csrf().disable()

            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

            .httpBasic().disable()  // 토큰 기반 인증이므로

             // 세션을 사용하지 않기 때문에 STATELESS로 설정
            .sessionManagement()  // session 기반이 아님을 선언
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // spring rest docs 경로
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/v1/oauth/login").permitAll()
            .requestMatchers("/v1/oauth/user/info").permitAll()
            .requestMatchers("/picture/**").permitAll()
            .requestMatchers("/spaces/**").permitAll()
            .requestMatchers("/getFollow/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .logout()
            .clearAuthentication(true);

        return http.build();
    }
}

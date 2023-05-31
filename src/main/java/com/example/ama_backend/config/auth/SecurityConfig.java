package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.JWTRequestFilter;
import com.example.ama_backend.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity //스프링 시큐리티 설정들을 활성화시킴
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 활성화한다
        http.cors()
                .and()
                // CSRF(Cross-Site Request Forgery) 보호를 설정한다
                .csrf()
                // CSRF 토큰을 쿠키에 저장하고,  "/v1/oauth/login" 엔드포인트를 CSRF 보호를 예외로 처리한다
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/v1/oauth/login")

                // 세션 관리를 STATELESS로 설정한다. 즉, 서버에 세션을 유지하지 않는다.
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // JWTRequestFilter 를 UsernamePasswordAuthenticationFilter 앞에 추가한다.
                // 즉, 요청을 처리하기 전에 JWT 토큰 필터가 먼저 실행된다
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                // 인증에 필요한 모든 요청에 대한 접근 제한을 설정한다.
                .authorizeHttpRequests()
                .requestMatchers("/v1/oauth/login").permitAll() // "/v1/oauth/login" 엔드포인트는 모든 사용자에게 허용한다
                .anyRequest().authenticated(); // 그 외의 모든 요청은 인증된 사용자만 접근 가능하다.

        return http.build();

    }

}

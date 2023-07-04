package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.JWTRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity //스프링 시큐리티 설정들을 활성화시킴
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// CORS 활성화
        http.cors()
                .and()
// CSRF(Cross-Site Request Forgery) 보호를 설정한다
                .csrf()
// CSRF 토큰을 쿠키에 저장하고, "/v1/oauth/login" 엔드포인트를 CSRF 보호를 예외로 처리한다
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/v1/oauth/login", "/h2-console") // "/v1/oauth/login"과 "/h2-console/"는 CSRF 보호 예외로 처리한다
                .and()
// 세션 관리를 STATELESS로 설정한다. 즉, 서버에 세션을 유지하지 않는다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
// JWTRequestFilter를 UsernamePasswordAuthenticationFilter 앞에 추가한다.
// 즉, 요청을 처리하기 전에 JWT 토큰 필터가 먼저 실행된다
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers("/spaces/**").permitAll()
                .requestMatchers("/spaces/isFollow/**").permitAll()
                .requestMatchers("/spaces/followingNumber/**").permitAll()
                .requestMatchers("/spaces/followerNumber/**").permitAll()
                .requestMatchers("/spaces/user/update/**").permitAll()
                .requestMatchers("/v1/oauth/login").permitAll() // "/v1/oauth/login" 엔드포인트는 모든 사용자에게 허용한다
                .requestMatchers("/static/**", "asset-manifest.json", "favicon.ico", "google-logo.svg", "index.html", "manifest.json", "robots.txt").permitAll()
                .requestMatchers("/h2-console").permitAll() // "/h2-console/" 엔드포인트도 모든 사용자에게 허용한다
                .anyRequest().authenticated()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .deleteCookies("JSESSIONID");
        // H2 Console 접근 설정
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }
}

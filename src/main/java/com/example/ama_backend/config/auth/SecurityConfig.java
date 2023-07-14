package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.JWTRequestFilter;
import org.springframework.context.annotation.Bean;
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
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()  // 토큰 기반 인증이므로
                .sessionManagement()  // session 기반이 아님을 선언
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/v1/oauth/login").permitAll()
                .requestMatchers("/v1/oauth/user/info").permitAll()
                .requestMatchers("/spaces/**").permitAll()
                .requestMatchers("/getFollow/**").permitAll()
                .requestMatchers("/spaces/user/update/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .clearAuthentication(true);
//                .and()
//                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

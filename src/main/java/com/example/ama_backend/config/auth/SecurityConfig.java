package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.JWTRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsUtils;

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
        http
                .cors().and()
                .requiresChannel().anyRequest().requiresSecure().and() // HTTPS 설정 추가
                .csrf().disable()// 세션 관리를 STATELESS로 설정한다. 즉, 서버에 세션을 유지하지 않는다.
// 요청을 처리하기 전에 JWTRequestFilter 실행
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers("/v1/oauth/login").permitAll()
                .requestMatchers("/v1/oauth/user/info").permitAll()
                .requestMatchers("/spaces/**").permitAll()
                .requestMatchers("/getFollow/**").permitAll()
                .requestMatchers("/spaces/user/update/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .deleteCookies("JSESSIONID");
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }
}

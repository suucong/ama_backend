package com.example.ama_backend.config.auth;

import com.example.ama_backend.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@EnableWebSecurity //스프링 시큐리티 설정들을 활성화시킴
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    @Autowired
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                //h2-console 화면을 사용하기 위해 해당 옵션들을 disable
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                // URL 별 권한 관리를 설정하는 옵션의 시작점
                // authorizeHttpRequests 가 선언되어야만 requestMatchers 옵션을 사용할 수 있음
                .authorizeHttpRequests()
                // 권한 관리 대상을 지정하는 옵션
                // URL, HTTP 메소드 별로 관리가 가능
                // "/" 등 지정된 URL들은 permitAll() 옵션을 통해 전체 열람 권한을 줌
                .requestMatchers("/", "/css.css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                // "/api/v1/**" 주소를 가진 API 는 USER 권한을 가진 사람만 가능하도록 설정
                .requestMatchers("/api/v1/**").hasRole(Role.USER.name())

                // 설정된 값들 이외 나머지 URL들을 나타냄
                // 여기서는 authenticated 를 추가하여 나머지 URL들은 모두 인증된 사용자들에게만 허용하게 함
                // authenticated(인증된 사용자) = 로그인한 사용자
                .anyRequest().authenticated()
                .and()
                // 로그아웃 기능에 대한 여러 설정의 진입점
                // 로그아웃 성공 시 / 주소로 이동함
                .logout()
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                     .deleteCookies("JSESSIONID")
                    .permitAll()
                .and()
                // OAuth2 로그인 기능에 대한 여러 설정들의 진입점
                .oauth2Login()
                // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                .userInfoEndpoint()

                // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록
                // 리소스 서버(즉, 소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시 할 수 있음
                .userService(customOAuth2UserService);

        return http.build();

    }

}

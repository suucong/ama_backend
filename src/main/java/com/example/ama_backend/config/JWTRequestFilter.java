package com.example.ama_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    // 토큰 검증에 사용된다.
    public JWTRequestFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // 이 필터는 들어오는 요청을 가로채고, "AUTH-TOKEN" 쿠키에서 JWT 토큰을 확인한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        // "AUTH-TOKEN" 쿠키를 찾는다. 존재하지 않을 경우 null 반환한다
        Cookie authCookie= cookies ==null? null : Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("AUTH-TOKEN"))
                .findAny().orElse(null);

        Authentication authentication;

        // "AUTH-TOKEN" 쿠키가 존재하고 토큰이 유효할 경우, 인증 정보를 SecurityContextHolder 에 설정한다
        if(authCookie != null && (authentication = jwtUtils.verifyAndGetAuthentication(authCookie.getValue())) != null){
                // 애플리케이션에서 이후의 인가 확인에 사용할 수 있도록 한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 필터 체인을 계속 진행하고 요청이 다음 필터나 대상 컨트롤러로 전달될 수 있도록 한다.
        // JWT 토큰을 확인하고, 인증된 사용자를 보다시피 보안 텍스트에 설정하여 애플리케이션에 추가적인 인가 확인을 수행하는 역할을 담당한다.
        filterChain.doFilter(request, response);
    }
}

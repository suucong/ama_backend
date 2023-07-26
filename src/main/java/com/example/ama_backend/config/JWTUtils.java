package com.example.ama_backend.config;


import com.example.ama_backend.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTUtils {
    private static final long TOKEN_VALIDITY=86400000L; // 토큰의 유효 기간을 설정한다. 여기선 24시간(1일)이다.
    private static final long TOKEN_VALIDITY_REMEMBER = 2592000000L; // "Remember Me" 기능을 위한 토큰의 유효 기간을 설정한다. 여기서는 30일이다.
    private final Key key; // JWT 서명을 위한 키이다.


    // JWTUtils 클래스의 생성자다. 애플리케이션의 JWT 시크릿 값을 인자로 받아 초기화한다.
    public JWTUtils(@Value("${app.jwtSecret}") String secret) {
        // @Value 어노테이션을 사용하여 app.jwtSecret 프로퍼티 값을 주입받는다.
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 주어진 UserEntity 정보를 기반으로 JWT 토큰을 생성하는 메소드이다
    public String createToken(UserEntity user, boolean rememberMe){
        long now= (new Date()).getTime(); // 현재 시간을 가져온다.
        Date validity = rememberMe ? new Date(now + TOKEN_VALIDITY_REMEMBER) : new Date(now + TOKEN_VALIDITY); //토큰의 유효 기간을 설정한다.
        Map<String, Object> claims = new HashMap<>(); // 클레임(클레임은 토큰에 추가되는 정보)을 저장하기 위한 맵을 생성한다.
        claims.put("role", user.getRole()); // 토큰에 저장할 클레임을 추가한다.

        return Jwts.builder()
                .setHeaderParam("typ", Header.JWT_TYPE)     ////// 2023.06.27 추가
                .setSubject(user.getId().toString()) // 토큰의 주체(Subject) 설정
                .setIssuedAt(new Date()) // 토큰이 발급된 시간을 설정
                .setExpiration(validity) // 토큰의 만료 시간을 설정
                .addClaims(claims) // 토큰에 클레임을 추가
                .signWith(key, SignatureAlgorithm.HS512) // 토큰에 서명을 추가
                .compact(); // 토큰을 문자열로 변환하여 반환한다.
    }

    // 주어진 토큰을 검증하고 인증(Authentication) 객체를 반환하는 메소드이다.
    public Authentication verifyAndGetAuthentication(String token){
        try{
           Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // 서명에 사용할 키를 설정합니다.
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 토큰에서 권한 정보를 추출해서 GrantedAuthority 리스트로 변환한다.
            List<GrantedAuthority> authorities= AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities); // 인증 객체를 생성하여 반환한다.
        }catch(JwtException | IllegalArgumentException ignored){
            return  null;
        }
    }

}

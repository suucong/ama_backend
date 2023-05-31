package com.example.ama_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // addCorsMappings : CORS 설정을 추가하는 메소드이다.
    @Override
    public void addCorsMappings(CorsRegistry registry){
        // CORS(Cross-Origin Resource Sharing) 설정을 추가한다.
        registry.addMapping("/**") // 모든 경로에 대해 CORS 설정을 적용한다.
                .allowedOrigins("http://localhost:3000") // 허용할 origin(도메인)을 설정한다.
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 허용할 HTTP METHOD
                .allowedHeaders("*") // 허용할 요청 헤더. 여기서는 모든 헤더를 허용한다.
                .allowCredentials(true); // 자격 증명(COOKIE, HTTP 인증 등)을 허용한다.
    }
}

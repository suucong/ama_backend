package com.example.ama_backend.controller;

import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

@CrossOrigin(originPatterns = "http://localhost:8080")
@RestController
@RequestMapping("/v1/oauth")
public class UserController {
    @Autowired
    private QAService qaService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    // Google OAuth를 통해 받은 ID 토큰으로 로그인을 처리하는 메소드이다.
    @PostMapping("/login")
    public ResponseEntity LoginWithGoogleOAuth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) throws GeneralSecurityException, IOException {

        // IdTokenRequestDto 는 요청 바디에서 받아온 ID 토큰을 담고 있다.
        String authToken = userService.loginOAuthGoogle(requestBody);

        response.addHeader("Authorization", authToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/info")
    public ResponseEntity getUserInfo() {

        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (testAuthentication == null) {
            // principal이 null인 경우에 대한 처리 로직
            // 예를 들어, 인증되지 않은 사용자에게 에러 응답을 반환하거나 다른 처리를 수행할 수 있습니다.
            System.out.println("principal null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // Principal 객체를 파라미터로 받아와서 사용자의 식별자로 사용한다
            // 여기서는 사용자의 식별자를 Long 타입으로 변환하여 UserService 의 getUser 메소드를 호출한다
            long luser = Long.valueOf((String) testAuthentication.getPrincipal());
            UserEntity user = userService.getUser(luser);

            // 조회된 사용자 정보를 DTO로 변환하여 응답으로 반환한다.
            // ResponseEntity 를 사용하여 200 ok 응답과 함께 DTO를 응답 본문에 담아서 반환한다.

            return ResponseEntity.ok().body(convertToDto(user));

        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 세션을 무효화한다
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // authentication 을 지운다
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}

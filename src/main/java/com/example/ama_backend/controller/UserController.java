package com.example.ama_backend.controller;

import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import com.google.common.base.Optional;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;

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
//        String idToken = requestBody.getIdToken();
//        idToken = idToken.replace("\"", "");
//        IdTokenRequestDto idTokenRequestDto = new IdTokenRequestDto();
//        idTokenRequestDto.setIdToken(idToken); // idToken은 실제 값을 할당해야 합니다.

        String authToken = userService.loginOAuthGoogle(requestBody);
        /**
         * // 2023.06.27
        // 응답에 인증 토큰을 쿠키로 첨부한다
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .sameSite("None")
                .build();

//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader("Authorization", cookie.toString());     // 2023.06.06 추가

        return ResponseEntity.ok().build();
        */

        response.addHeader("Authorization", authToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/info")
    public ResponseEntity getUserInfo(Principal principal, Authentication authentication) {

        /**
         * 2023.06.27
        if (authentication == null) {
            // principal이 null인 경우에 대한 처리 로직
            // 예를 들어, 인증되지 않은 사용자에게 에러 응답을 반환하거나 다른 처리를 수행할 수 있습니다.
            System.out.println("principal null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Principal 객체를 파라미터로 받아와서 사용자의 식별자로 사용한다
        // 여기서는 사용자의 식별자를 Long 타입으로 변환하여 UserService 의 getUser 메소드를 호출한다
        UserEntity user = userService.getUser(Long.valueOf(principal.getName()));
        System.out.println(principal.getName());

        // 조회된 사용자 정보를 DTO로 변환하여 응답으로 반환한다.
        // ResponseEntity 를 사용하여 200 ok 응답과 함께 DTO를 응답 본문에 담아서 반환한다.
        return ResponseEntity.ok().body(convertToDto(user));
         */

        // 2023.06.27 사용자 정보 가져오기~~~~~~~~~
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (testAuthentication == null) {
            // principal이 null인 경우에 대한 처리 로직
            // 예를 들어, 인증되지 않은 사용자에게 에러 응답을 반환하거나 다른 처리를 수행할 수 있습니다.
            System.out.println("principal null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // Principal 객체를 파라미터로 받아와서 사용자의 식별자로 사용한다
            // 여기서는 사용자의 식별자를 Long 타입으로 변환하여 UserService 의 getUser 메소드를 호출한다
            long luser = Long.valueOf((String)testAuthentication.getPrincipal());
            UserEntity user = userService.getUser(luser);
//        System.out.println(principal.getName());

            // 조회된 사용자 정보를 DTO로 변환하여 응답으로 반환한다.
            // ResponseEntity 를 사용하여 200 ok 응답과 함께 DTO를 응답 본문에 담아서 반환한다.
            if (user != null) {
                return ResponseEntity.ok().body(convertToDto(user));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  //s
            }
        }
    }

//    @GetMapping("/user/info")
//    public ResponseEntity getUserInfo(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            // 인증되지 않은 사용자에게 에러 응답을 반환하거나 다른 처리를 수행할 수 있습니다.
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // 인증된 사용자의 정보를 가져옵니다.
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String username = userDetails.getUsername();
//
//        // 추가적인 처리를 수행하거나 응답을 반환할 수 있습니다.
//        return ResponseEntity.ok().body("Authenticated User: " + username);
//    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }

    /*
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return ResponseEntity.ok().build();
    }

/*
    @GetMapping("/spaces/{spaceId}/update")
    public String modify(@PathVariable Long spaceId, Model model, MultipartFile imgFile) throws Exception {
        //이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            UserEntity userEntity = userRepository.findByEmail(user.getEmail()).orElse(null);
            if (userEntity != null) {
                model.addAttribute("userId", userEntity.getId());
                model.addAttribute("userName", userEntity.getName());
                model.addAttribute("userIntroduce", userEntity.getIntroduce());
                model.addAttribute("userPicture", userEntity.getPicture());
                model.addAttribute("userProfileImgName", userEntity.getProfileImgName());
                model.addAttribute("userInstaId", userEntity.getInstaId());
                model.addAttribute("spaceId", space.getId());
             }
        }
        return "profile-edit";
    }
    */

}

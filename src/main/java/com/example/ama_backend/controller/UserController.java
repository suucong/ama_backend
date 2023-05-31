package com.example.ama_backend.controller;

import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

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
    public ResponseEntity LoginWithGoogleOAuth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        // IdTokenRequestDto 는 요청 바디에서 받아온 ID 토큰을 담고 있다.
        String authToken = userService.loginOAuthGoogle(requestBody);

        // 응답에 인증 토큰을 쿠키로 첨부한다
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    // 현재 인증된 사용자의 정보를 조회하는 메소드이다
    @GetMapping("/user/info")
    public ResponseEntity getUserInfo(Principal principal) {
        // Principal 객체를 파라미터로 받아와서 사용자의 식별자로 사용한다
        // 여기서는 사용자의 식별자를 Long 타입으로 변환하여 UserService 의 getUser 메소드를 호출한다
        UserEntity user = userService.getUser(Long.valueOf(principal.getName()));

        // 조회된 사용자 정보를 DTO로 변환하여 응답으로 반환한다.
        // ResponseEntity 를 사용하여 200 ok 응답과 함께 DTO를 응답 본문에 담아서 반환한다.
         return ResponseEntity.ok().body(convertToDto(user));
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

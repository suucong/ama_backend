package com.example.ama_backend.controller;

import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.dto.UserUpdateRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

@RestController
@CrossOrigin(origins = "https://mumul.space")
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
    @PostMapping(value = "/v1/oauth/login", consumes = "application/json")
    public ResponseEntity LoginWithGoogleOAuth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) throws GeneralSecurityException, IOException {
        System.out.println("v1/oauth/login 엔드포인트 되는지 확인");
        // IdTokenRequestDto 는 요청 바디에서 받아온 ID 토큰을 담고 있다.
        String authToken = userService.loginOAuthGoogle(requestBody);
        System.out.println(authToken);
        response.addHeader("Access-Control-Allow-Origin", "*");

        response.addHeader("Authorization", authToken);
        response.addHeader("Access-Control-Opener-Policy", "same-origin-allow-popups");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/oauth/user/info")
    public ResponseEntity getUserInfo() {
        System.out.println("getUserInfo");
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (testAuthentication == null || testAuthentication.getPrincipal() == "anonymousUser") {
            System.out.println("getUserInfo1");
            return ResponseEntity.ok().body(false);
        } else {
            System.out.println(">>>>>>>>>");
            System.out.println(testAuthentication.toString());
            System.out.println(testAuthentication.getPrincipal());

            long luser = Long.valueOf((String) testAuthentication.getPrincipal());
            UserEntity user = userService.getUser(luser);

            if(user!=null) return ResponseEntity.ok().body(convertToDto(user));
            else return ResponseEntity.ok().body(false);
        }
    }


    @PostMapping("/v1/oauth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        // 클라이언트 측에 저장된 토큰을 무효화하고 삭제하기 위해 응답 헤더에 토큰을 제거하는 코드 추가
        response.setHeader("Authorization", "");

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/picture/{spaceId}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<?> getProfileImg(@PathVariable Long spaceId) throws IOException {
        UserEntity user = userRepository.findById(spaceId).orElse(null);

        if (user != null && Objects.equals(user.getProfileByte(), "")) {
            return new ResponseEntity<>(user.getPicture(), HttpStatus.OK);
        } else {
            assert user != null;
            return new ResponseEntity<>(user.getProfileByte(), HttpStatus.OK);
        }
    }

    @PutMapping("/v1/oauth/user/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestPart(value = "requestDto") UserUpdateRequestDto requestDto, @RequestPart(value = "imgFile", required = false) MultipartFile imgFile) throws Exception {
        UserEntity currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 계정만 수정할 수 있습니다.");
        }

        currentUser.setName(requestDto.getName());
        currentUser.setIntroduce(requestDto.getIntroduce());
        currentUser.setInstaId(requestDto.getInstaId());
        currentUser.setLink(requestDto.getLink());

        if (imgFile == null) {
            userService.saveUserAccountWithoutProfile(currentUser);
        } else {
            userService.updatePicture(currentUser, imgFile);
        }

        return ResponseEntity.ok("수정이 완료되었습니다.");
    }

    @PutMapping("/v1/oauth/user/spaceStop/{userId}")
    public ResponseEntity<String> alterStopSpace(@PathVariable Long userId, @RequestBody boolean stopSpace) {
        UserEntity currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 계정만 수정할 수 있습니다.");
        }

        currentUser.setStopSpace(stopSpace);
        System.out.println(stopSpace);
        userService.saveUserAccountWithoutProfile(currentUser);

        return ResponseEntity.ok("스페이스 중지 변경이 완료되었습니다.");
    }

    @PutMapping("/v1/oauth/user/alertSpace/{userId}")
    public ResponseEntity<String> alterAlertSpace(@PathVariable Long userId, @RequestBody boolean alertSpace) {
        UserEntity currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 계정만 수정할 수 있습니다.");
        }

        currentUser.setAlertSpace(alertSpace);
        System.out.println(alertSpace);
        userService.saveUserAccountWithoutProfile(currentUser);

        return ResponseEntity.ok("스페이스 중지 변경이 완료되었습니다.");
    }

    @DeleteMapping("/v1/oauth/user/secession/{userId}")
    public ResponseEntity<String> userSecession(@PathVariable Long userId) {
        userService.doSecession(userId);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다. ");
    }
}

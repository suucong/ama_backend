package com.example.ama_backend.config;

import com.example.ama_backend.config.auth.CustomOAuth2UserService;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.UserUpdateRequestDto;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
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
    private CustomOAuth2UserService userService;

    @GetMapping("/")
    public String main(Model model){
        return "main";
    }

    @GetMapping("/signin")
    public String login(Model model) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser != null) {
            UserEntity userEntity = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);
            if (userEntity != null) {
                SpaceEntity space = spaceRepository.findByUserId(userEntity.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

                model.addAttribute("space", space);
                model.addAttribute("userName", userEntity.getName());
                model.addAttribute("userEmail", userEntity.getEmail());
                model.addAttribute("userPicture", userEntity.getPicture());
                model.addAttribute("spaceId", space.getId());
            }
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        /**
         * "redirect:/"는 스프링 프레임워크에서 제공하는 URL 리다이렉트 기능입니다.
         * 이 코드는 "/logout" 요청을 처리한 후, 다시 클라이언트 측으로 "redirect:/"" 응답을 보내게 됩니다.
         * 이 응답은 브라우저에서 다시 "/" URL로 요청을 보내게끔 유도하여, 클라이언트가 "/" URL로 리다이렉트 되도록 합니다.
         * 따라서, 로그아웃 후에는 다시 메인 페이지로 리다이렉트 되게 됩니다.*/
        return "redirect:/";
    }


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
}

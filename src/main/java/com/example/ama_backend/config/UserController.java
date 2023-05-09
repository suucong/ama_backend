package com.example.ama_backend.config;

import com.example.ama_backend.config.auth.CustomOAuth2UserService;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

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

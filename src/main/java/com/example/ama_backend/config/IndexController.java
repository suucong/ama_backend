package com.example.ama_backend.config;

import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class IndexController {

    @Autowired
    private QAService qaService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser != null) {
            UserEntity userEntity = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);
            SpaceEntity space = spaceRepository.findByUserId(sessionUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));


            model.addAttribute("space", space);
            model.addAttribute("userName", userEntity.getName());
            model.addAttribute("userEmail", userEntity.getEmail());
            model.addAttribute("userPicture", userEntity.getPicture());
            model.addAttribute("spaceId", space.getId());
        }


        return "index";
    }

    @GetMapping("/updatingForm")
    public String modify(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            // 현재 로그인한 유저의 고유 아이디로 스페이스 엔터티 가져오기
            SpaceEntity space = spaceRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            model.addAttribute("userId", user.getId());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userIntroduce", user.getIntroduce());
            model.addAttribute("userPicture", user.getPicture());
            model.addAttribute("userInstaId", user.getInstaId());
            model.addAttribute("spaceId", space.getId());
        }
        return "profile-edit";
    }
}

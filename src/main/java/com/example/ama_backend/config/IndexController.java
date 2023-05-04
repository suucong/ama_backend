package com.example.ama_backend.config;

import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final QAService qaService;
    private final HttpSession httpSession;
    @Autowired
    private SpaceRepository spaceRepository;


    @GetMapping("/")
    public String index(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
       Long spaceId;
        if (user != null) {
            // 현재 로그인한 유저의 고유 아이디로 스페이스 엔터티 가져오기
            SpaceEntity space = spaceRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            model.addAttribute("space", space);
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail",user.getEmail());
            model.addAttribute("userPicture",user.getPicture());
            model.addAttribute("spaceId",space.getId());
        }

        return "index";
    }
    @GetMapping("/modify")
    public String modify(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userIntroduce",user.getIntroduce());
            model.addAttribute("userPicture",user.getPicture());
            model.addAttribute("userInstaId", user.getInstaId());
        }
        return "/profile-edit";
    }
}

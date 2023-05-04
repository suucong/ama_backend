package com.example.ama_backend.config;

import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final QAService qaService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        //model.addAttribute("posts", qaService.getAllQuestions());
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail",user.getEmail());
            model.addAttribute("userPicture",user.getPicture());
            model.addAttribute("userId", user.getId());
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

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
//    @GetMapping
//    public ResponseEntity<String> hello() {
        // 앞서 작선된 CustomOAuth2Service 에서 로그인 성공 시 세션에 SessionUser 를 저장하도록 구성함
        // 즉, 로그인 성공 시 httpSession.getAttribute("user")에서 값을 가져올 수 있다.
       // SessionUser user = (SessionUser) httpSession.getAttribute("user");

        // 세션에 저장된 값이 있을 때만 model 에 userName 으로 등록
//        if (user != null) {
//            model.addAttribute("userName", user.getEmail());
//        }
//
//        if (user != null) {
//            return ResponseEntity.ok("유저 이름: "+user.getNickname());
//        }else{
//            return ResponseEntity.ok("유저 이름: 익명 d");
//        }
          //  return ResponseEntity.ok("로그인 성공");
    //}
   // private final HttpSession httpSession;

//    @GetMapping
//    public String index() {
//        return "";
//    }
//
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
//
//    @GetMapping({"/loginSuccess", "/hello"})
//    public String loginSuccess(Model model) {
//        // 앞서 작선된 CustomOAuth2Service 에서 로그인 성공 시 세션에 SessionUser 를 저장하도록 구성함
//        // 즉, 로그인 성공 시 httpSession.getAttribute("user")에서 값을 가져올 수 있다.
////        SessionUser user = (SessionUser) httpSession.getAttribute("user");
////
////        // 세션에 저장된 값이 있을 때만 model 에 userName 으로 등록
////        if (user != null) {
////            model.addAttribute("userName", user.getEmail());
////        }
//
//       return "hello";
//    }

//    @GetMapping("/loginFailure")
//    public String loginFailure() {
//        return "loginFailure";
//    }
//    @GetMapping
//    public ResponseEntity<String> hello(Model model) {
//        // 앞서 작선된 CustomOAuth2Service 에서 로그인 성공 시 세션에 SessionUser 를 저장하도록 구성함
//        // 즉, 로그인 성공 시 httpSession.getAttribute("user")에서 값을 가져올 수 있다.
//        SessionUser user = (SessionUser) httpSession.getAttribute("user");
//
//        // 세션에 저장된 값이 있을 때만 model 에 userName 으로 등록
//        if (user != null) {
//            model.addAttribute("userName", user.getEmail());
//        }
//
//        if (user != null) {
//            return ResponseEntity.ok("유저 이름: "+user.getNickname());
//        }else{
//            return ResponseEntity.ok("유저 이름: 익명 d");
//        }
//    }



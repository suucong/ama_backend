package com.example.ama_backend.controller;

import com.example.ama_backend.config.auth.CustomOAuth2UserService;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.AnswerDTO;
import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.entity.QuestionEntity;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.QuestionRepository;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.service.QAService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/spaces")
public class SpaceController {

    @Autowired
    private QAService qaService;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;


    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }


    // 내가 보낸 질문 조회
    public ResponseEntity<ResponseDTO<QuestionDTO>> getMySentQuestions(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findBySendingUserId(ownerUserId);
            List<QuestionDTO> questionDTOs = questionEntities.stream().map(QuestionDTO::new).collect(Collectors.toList());
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(questionDTOs).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    public ResponseEntity<ResponseDTO<QuestionDTO>> getMyReceivedQuestions(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findByReceivingUserId(ownerUserId);
            List<QuestionDTO> questionDTOs = questionEntities.stream().map(QuestionDTO::new).collect(Collectors.toList());
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(questionDTOs).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @GetMapping("/{id}")
    public String qnaForm(@PathVariable("id") Long spaceId, Model model, HttpSession session) {
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        SessionUser sessionUser = (SessionUser) session.getAttribute("user");
        UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 스페이스가 현재 로그인한 소유한 스페이스라면
        if (space.isOwnedBy(user)) {
            assert user != null;
            model.addAttribute("isOwner", true);
            model.addAttribute("userName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        // 현재 스페이스가 현재 로그인한 소유한 스페이스가 아니라면
        else {
            model.addAttribute("isOwner", false);
            model.addAttribute("ownerName", ownerUser.getName());
            model.addAttribute("ownerEmail", ownerUser.getEmail());
        }


        model.addAttribute("space", space);

        assert user != null;
        model.addAttribute("sentQuestions", getMySentQuestions(spaceId).getBody().getData());
        model.addAttribute("receivedQuestions", getMyReceivedQuestions(spaceId).getBody().getData());
        return "space";
    }

    // 답변 등록 API
    @PostMapping("{questionId}/answer/create")
    public ResponseEntity<?> createAnswer(@RequestBody AnswerDTO answerDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // provider (google, kakao 등) 정보 가져오기
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String name = oauthUser.getAttribute("name");


            // AnswerEntity 로 변환
            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);

            // id를 null 로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            answerEntity.setId(null);

            // 현재 구글로 로그인한 유저의 네임
            answerEntity.setUserId(name);


            // 서비스를 이용해 질문 엔티티를 생성한다
            List<AnswerEntity> entities = qaService.saveAnswer(answerEntity);

            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 답변 삭제 API
    @DeleteMapping("{answerId}/answer/delete")
    public ResponseEntity<?> deleteAnswer(@RequestParam Long answerId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // provider (google, kakao 등) 정보 가져오기
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String name = oauthUser.getAttribute("name");


            // 서비스를 이용해 질문 엔티티를 생성한다
            List<AnswerEntity> entities = qaService.deleteAnswer(answerId);

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 리스트로 변환한다.
            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 질문 등록 API
    @PostMapping("/question/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO questionDTO,
                                            @RequestParam(name = "anonymous", required = false, defaultValue = "false") boolean isAnonymous) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // provider (google, kakao 등) 정보 가져오기
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String name = oauthUser.getAttribute("name");


            // QuestionEntity 로 변환
            QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);

            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            questionEntity.setId(null);

            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
            questionEntity.setUserId(name);

            // 서비스를 이용해 질문 엔티티를 생성한다
            List<QuestionEntity> entities = qaService.saveQuestion(questionEntity, isAnonymous);

            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 질문 삭제 API - 남이 보낸 질문이라도 삭제 기능이 있다.
    @DeleteMapping("{questionId}/question/delete")
    public ResponseEntity<?> deleteQuestion(@RequestParam Long questionId) {
        try {
            // 서비스를 이용해 질문 엔티티를 생성한다
            List<QuestionEntity> entities = qaService.deleteQuestionAndAnswers(questionId);

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 리스트로 변환한다.
            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}

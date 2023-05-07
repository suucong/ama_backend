package com.example.ama_backend.controller;

import com.example.ama_backend.config.auth.CustomOAuth2UserService;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.AnswerDTO;
import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.dto.UserUpdateRequestDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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


    @GetMapping("/{spaceId}")
    public String qnaForm(@PathVariable Long spaceId, Model model, HttpSession session) {
        //이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        // 이동한 스페이스의 주인유저 엔터티
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 세션유저
        SessionUser sessionUser = (SessionUser) session.getAttribute("user");
        //현제로그인한 세션유저로 찾은 현재 유저 엔터티
        UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);


        // 현재 스페이스가 현재 로그인한 소유한 스페이스라면
        if (space.isOwnedBy(user)) {
            assert user != null;
            model.addAttribute("isOwner", true);
            model.addAttribute("picture", user.getPicture());
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("introduce", user.getIntroduce());
            model.addAttribute("instaId", user.getInstaId());
            model.addAttribute("role", user.getRole());
        }
        // 현재 스페이스가 현재 로그인한 소유한 스페이스가 아니라면
        else {
            model.addAttribute("isOwner", false);
            model.addAttribute("picture", ownerUser.getPicture());
            model.addAttribute("name", ownerUser.getName());
            model.addAttribute("email", ownerUser.getEmail());
            model.addAttribute("introduce", ownerUser.getIntroduce());
            model.addAttribute("instaId", ownerUser.getInstaId());
            model.addAttribute("role", ownerUser.getRole());
            model.addAttribute("spaceId", space.getId());
        }


        model.addAttribute("space", space);

        assert user != null;
        model.addAttribute("sentQuestions", getMySentQuestions(spaceId).getBody().getData());
        model.addAttribute("receivedQuestions", getMyReceivedQuestions(spaceId).getBody().getData());
        return "space";
    }


    // UserEntity 수정
    @PutMapping("/user/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequestDto requestDto) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        UserEntity currentUser = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 계정만 수정할 수 있습니다.");
        }

        currentUser.setName(requestDto.getName());
        currentUser.setIntroduce(requestDto.getIntroduce());
        currentUser.setInstaId(requestDto.getInstaId());
        currentUser.setPicture(requestDto.getPicture());

        userRepository.save(currentUser);

        return ResponseEntity.ok("수정이 완료되었습니다.");
    }

    @PostMapping("/{spaceId}/{questionId}/answer")
    public String AnswerInput(@PathVariable Long spaceId,@PathVariable Long questionId,Model model){
        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 답변달 질문 엔터티
        QuestionEntity question=questionRepository.findById(questionId)
                .orElseThrow(()->  new IllegalArgumentException("Invalid question id"));

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            // 현재 로그인한 유저 엔터티
            UserEntity userEntity = userRepository.findByEmail(user.getEmail()).orElse(null);
            if (userEntity != null) {
                // 현재 로그인한 유저의 스페이스 엔터티


                model.addAttribute("sendingUserId", userEntity.getId());
                model.addAttribute("sendingUserName", userEntity.getName());
                model.addAttribute("sendingUserPicture", userEntity.getPicture());
                model.addAttribute("questionId", question.getId());
                model.addAttribute("receivingUserSpaceId", space.getId());

            }

        }
        return "question";
    }

    // 답변 등록 API
    @PostMapping("{questionId}/answer/create")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId,@RequestBody AnswerDTO answerDTO) {
        try {


            // AnswerEntity 로 변환
            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);

            // id를 null 로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            answerEntity.setId(null);

            // 현재 구글로 로그인한 유저의 네임
           // answerEntity.setUserId(name);


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

    @GetMapping("/{spaceId}/question")
    public String questionInput(@PathVariable Long spaceId, Model model) {
        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            // 현재 로그인한 유저 엔터티
            UserEntity userEntity = userRepository.findByEmail(user.getEmail()).orElse(null);
            if (userEntity != null) {
                // 현재 로그인한 유저의 스페이스 엔터티


                model.addAttribute("sendingUserId", userEntity.getId());
                model.addAttribute("sendingUserName", userEntity.getName());
                model.addAttribute("sendingUserPicture", userEntity.getPicture());
                // model.addAttribute("sendingUserSpaceId", space.getId());
                model.addAttribute("receivingUserSpaceId", space.getId());

            }

        }
        return "question";
    }

    // 질문 등록 API
    @PostMapping("/{spaceId}/question/create")
    public ResponseEntity<?> createQuestion(@PathVariable Long spaceId, @RequestBody QuestionDTO questionDTO, HttpSession session) {

        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 세션 유저
        SessionUser sessionUser = (SessionUser) session.getAttribute("user");

        // 세션 유저의 이메일로 현재 로그인한 유저 엔터티 찾기
        UserEntity currentUser = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);


        try {

            // QuestionEntity 로 변환
            QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);

            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            questionEntity.setId(null);

            assert currentUser != null;
            // 질문 보내는 사람 이름 설정
            questionEntity.setUserId(currentUser.getName());
            // 질문보내는 사람 아이디 설정
            questionEntity.setSendingUserId(currentUser.getId());
            //질문 받는 사람 아이디 설정
            questionEntity.setReceivingUserId(spaceUser.getId());
            //답변 null 설정
            questionEntity.setAnswers(null);
            //현재 시각 설정
            questionEntity.setCreatedTime(LocalDateTime.now());

            //질문 보내는 사람 프사 설정
            questionEntity.setSentUserPic(currentUser.getPicture());

            // 서비스를 이용해 질문 엔티티를 생성한다
            List<QuestionEntity> entities = qaService.saveQuestion(questionEntity);

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

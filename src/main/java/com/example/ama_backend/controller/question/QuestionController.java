package com.example.ama_backend.controller.question;

import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.entity.QuestionEntity;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.*;
import com.example.ama_backend.service.FollowService;
import com.example.ama_backend.service.MailService;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/spaces")
@Slf4j
public class QuestionController {

    @Autowired
    private QAService qaService;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MailService mailService;



    // 질문 등록 API
    @PostMapping("/{spaceId}/question/create")
    public ResponseEntity<?> createQuestion(@PathVariable Long spaceId, @RequestBody QuestionDTO questionDTO) {

        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현 로그인한 유저
        //TODO : 로그아웃 후에 다른 계정으로 재로그인 시도 시 testAuthentication null 이어서 else 문 작동되는 듯
        Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("testAuthentication: " +testAuthentication);
        System.out.println("questionDTO: "+questionDTO);

        if (testAuthentication != null) {
            // 현재 로그인한 유저 아이디
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());

            // 현 로그인한 유저의 아이디로 현 로그인한 유저 엔터티 찾기  -- 질문할 유저
            UserEntity currentUser = userService.getUser(currentUserId);


            // 로그인을 한 상태라면
            if (currentUser != null) {
                try {

                    // QuestionEntity 로 변환
                    QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);

                    // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
                    questionEntity.setId(null);


                    //질문 받는 사람 아이디 설정
                    questionEntity.setReceivingUserId(spaceUser.getId());

                    // 질문 하는 사람 아이디 설정
                    questionEntity.setSendingUserId(currentUser.getId());

                    System.out.println("questionEntity: "+ questionEntity);


                    // 서비스를 이용해 질문 엔티티를 생성한다
                    List<QuestionEntity> entities = qaService.saveQuestion(questionEntity);

                    // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
                    List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());

                    // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
                    ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

                    if (spaceUser.isAlertSpace()) {
                        String mailTop = spaceUser.getName() + "님의 스페이스에 새로운 질문이 생성되었습니다.";
                        String toAddress = spaceUser.getEmail();
                        String mailContent = currentUser.getName() + "님이 회원님의 스페이스에 질문을 생성했습니다.";

                        mailService.mailSend(toAddress, mailTop, mailContent);
                    }

                    // ResponseDTO 를 리턴한다.
                    return ResponseEntity.ok().body(responseDTO);
                } catch (Exception e) {
                    // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
                    String err = e.getMessage();
                    ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
                    return ResponseEntity.badRequest().body(responseDTO);
                }
            }
            // 로그인을 안한 상태라면
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  //s
            }
        } else {
            return ResponseEntity.ok().body("질문을 하기 위해서는 로그인이 필수입니다.");
        }


    }


    @GetMapping("/{spaceId}/received/get")
    public ResponseEntity<ResponseDTO<QuestionDTO>> getReceivedQuestion(@PathVariable Long spaceId) {
        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        try {
            // QuestionEntity 로 변환
            List<QuestionEntity> questionList = qaService.getMyReceivingQuestions(spaceUser.getId());

            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 로 변환한다.
            List<QuestionDTO> dtos = questionList.stream().map(QuestionDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }




    // 보낸 질문과 답변 조회 api
    @GetMapping("/{spaceId}/sent/get")
    public ResponseEntity<?> getSentQuestion(@PathVariable Long spaceId) {

        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저
        UserEntity spaceUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));


        try {

            // QuestionEntity 로 변환
            List<QuestionEntity> questionList =qaService.getMySendingQuestions(spaceUser.getId());

            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<QuestionDTO> dtos = questionList.stream().map(QuestionDTO::new).collect(Collectors.toList());

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



    // 질문 삭제 API
    // 남이 보낸 질문이라도 내 스페이스 내라면 삭제 가능
    // 이동한 스페이스에서 내가 보낸 질문이라면 삭제 가능
    @DeleteMapping("/{spaceId}/{questionId}/{userId}/question/delete")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long userId,@PathVariable Long spaceId, @PathVariable Long questionId) {
        try {
            UserEntity currentUser = userRepository.findById(userId).orElse(null);

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
            }


            // 이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // questionId로 질문엔터티 조회
           QuestionEntity question=questionRepository.findById(questionId).orElse(null);

       ;

            // 이동한 스페이스 주인아이디로 유저엔터티 찾기 -- 질문 받는 스페이스 주인 유저(삭제권한)
            UserEntity spaceUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));


            // 현재 스페이스가 내 스페이스라면
            if (space.isOwnedBy(currentUser)) {
                // 서비스를 이용해 질문 엔티티를 삭제한다
                qaService.deleteQuestionAndAnswers(questionId);
                return ResponseEntity.ok().body("내 스페이스의 질문을 삭제했습니다.");
            }
            // 현재 스페이스가 내 스페이스가 아니라면
            else{
                if(question!=null){
                    // 삭제하려는 질문이 내가 작성한 질문이면
                    if(question.isMyQuestion(currentUser.getId())==true){
                        qaService.deleteQuestionAndAnswers(questionId);
                        return ResponseEntity.ok().body("내가 작성한 질문을 삭제했습니다.");
                    }else{
                        return ResponseEntity.badRequest().body("본인이 작성한 질문만 삭제 가능합니다.");
                    }
                }else{
                    return ResponseEntity.ok().body("삭제할 질문이 존재하지 않습니다.");
                }

            }
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }


    }


}

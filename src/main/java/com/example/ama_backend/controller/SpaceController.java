package com.example.ama_backend.controller;


import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.AnswerDTO;
import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.dto.UserUpdateRequestDto;
import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.*;
import com.example.ama_backend.service.FollowService;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.ama_backend.config.auth.CustomOAuth2UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

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
    private UserService userService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowService followService;
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

    //내가 받은 질문 조회
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

    // 내가 보낸 질문에 대한 답변 조회
    public ResponseEntity<ResponseDTO<AnswerDTO>> getMySentAnswer(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findBySendingUserId(ownerUserId);

            List<AnswerDTO> answerDTOS = new ArrayList<>();

            // 각 질문에 대한 답변 리스트 가져오기
            for (QuestionEntity question : questionEntities) {
                List<AnswerEntity> answerEntities = question.getAnswers();
                List<AnswerDTO> answers = answerEntities.stream().map(AnswerDTO::new).toList();
                answerDTOS.addAll(answers);
            }

            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(answerDTOS).build();

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내가 받은 질문에 대한 답변 조회
    public ResponseEntity<ResponseDTO<AnswerDTO>> getMyReceivedAnswer(Long spaceId) {
        try {
            // 해당 스페이스의 주인 유저의 고유 아이디 가져오기
            SpaceEntity space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
            Long ownerUserId = space.getUserId();

            List<QuestionEntity> questionEntities = questionRepository.findByReceivingUserId(ownerUserId);

            List<AnswerDTO> answerDTOS = new ArrayList<>();

            // 각 질문에 대한 답변 리스트 가져오기
            for (QuestionEntity question : questionEntities) {
                List<AnswerEntity> answerEntities = question.getAnswers();
                List<AnswerDTO> answers = answerEntities.stream().map(AnswerDTO::new).toList();
                answerDTOS.addAll(answers);
            }

            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(answerDTOS).build();

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @GetMapping("/{spaceId}")
    public ResponseEntity getSpaceInfo(@PathVariable Long spaceId) throws Exception {
        //이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        // 이동한 스페이스의 주인유저 엔터티
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 유저
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if(testAuthentication!=null){
            // 현재 로그인한 유저 아이디
            long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

            UserEntity user = userService.getUser(ownerUser.getId());
            System.out.println("owneruser.getid: "+ownerUser.getId());
            System.out.println("currentuserid:"+currentUserId);
            return ResponseEntity.ok().body(convertToDto(user));
        }else{
            return ResponseEntity.ok().body(convertToDto(ownerUser));
        }
    }




//    // TODO: 프로필이미지 가져오기

//    @GetMapping(value = "/{spaceId}/picture", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
//    public ResponseEntity<?> getProfileImg(@PathVariable Long spaceId) throws IOException {
//        UserEntity user = userRepository.findById(spaceId).orElse(null);
//
//        if (user != null && Objects.equals(user.getProfileImgName(), "")) {
//            return new ResponseEntity<>(user.getPicture(), HttpStatus.OK);
//        } else {
//            assert user != null;
//            InputStream inputStream = new FileInputStream(user.getPicture());
//            byte[] imageByteArray = IOUtils.toByteArray(inputStream);
//            inputStream.close();
//            return new ResponseEntity<>(imageByteArray, HttpStatus.OK);
//        }
//    }

    @GetMapping(value = "/{spaceId}/picture", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<?> getProfileImg(@PathVariable Long spaceId) throws IOException {
        UserEntity user = userRepository.findById(spaceId).orElse(null);

        if (user != null && Objects.equals(user.getProfileByte(), "")) {
            return new ResponseEntity<>(user.getPicture(), HttpStatus.OK);
        } else {
            assert user != null;
            return new ResponseEntity<>(user.getProfileByte(), HttpStatus.OK);
        }
    }


    @PostMapping("/{spaceId}/follow")
    public ResponseEntity<String> follow(@PathVariable Long spaceId, @RequestBody Map<String, Long> requestData) {
        try {
            //이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 이동한 스페이스의 주인유저 엔터티(toUser)
            UserEntity ownerUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

            // 현재 로그인한 유저;
//            org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
//            long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

            Long currentUserId = requestData.get("currentUserId");

            UserEntity user = userService.getUser(currentUserId);
            System.out.println("owneruser.getid: "+ownerUser.getId());
            System.out.println("currentuserid:"+currentUserId);

            //팔로우하기
            assert user != null;
            followService.follow(user, ownerUser);

            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("bad");
        }
    }

    @PostMapping("/{spaceId}/unFollow")
    public ResponseEntity<String> unFollow(@PathVariable Long spaceId, @RequestBody Map<String, Long> requestData) {
        try {
            //이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 이동한 스페이스의 주인유저 엔터티
            UserEntity ownerUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));


            Long currentUserId = requestData.get("currentUserId");

            UserEntity user = userService.getUser(currentUserId);
            System.out.println("owneruser.getid: "+ownerUser.getId());
            System.out.println("currentuserid:"+currentUserId);

            followService.deleteFollow(user, ownerUser);

            //세션에서 현재 유저정보 가져오기
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("bad");
        }
    }

    @GetMapping("/{spaceId}/isFollow")
    public ResponseEntity<Boolean> isFollow(@PathVariable Long spaceId, @RequestBody Map<String, Long> requestData) {
        Long currentUserId = requestData.get("currentUserId");
        UserEntity currentUser = userService.getUser(currentUserId);

        UserEntity spaceUser = userService.getUser(spaceId);

        boolean isFollowed = followRepository.findByFromUserAndToUser(currentUser, spaceUser).isPresent();

        return ResponseEntity.ok().body(isFollowed);
    }

    @PutMapping("/user/update/{userId}")
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
            customOAuth2UserService.saveUserAccountWithoutProfile(currentUser);
        } else {
            customOAuth2UserService.updatePicture(currentUser, imgFile);
        }

        return ResponseEntity.ok("수정이 완료되었습니다.");
    }



//
//    @GetMapping("/{spaceId}/{questionId}/answer")
//    public String AnswerInput(@PathVariable Long spaceId, @PathVariable Long questionId, Model model) {
//        // 이동한 스페이스 엔터티
//        SpaceEntity space = spaceRepository.findById(spaceId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
//
//        // 답변달 질문 엔터티
//        QuestionEntity question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid question id"));
//
//        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
//        //현재 로그인한 세션유저로 찾은 현재 유저 엔터티
//        UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);
//
//        //현재 스페이스가 내 스페이스라면
//        if (space.isOwnedBy(user)) {
//            assert user != null;
//            model.addAttribute("sendingUserName", user.getName());
//            model.addAttribute("sendingUserPicture", user.getPicture());
//            model.addAttribute("userId", user.getId());
//            model.addAttribute("questionId", question.getId());
//            model.addAttribute("spaceId", space.getId());
//        }
//
//        return "answer";
//    }

    @GetMapping("/{spaceId}/{questionId}/answer")
    public String AnswerInput(@PathVariable Long spaceId, @PathVariable Long questionId, Model model) {
        // 이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 답변달 질문 엔터티
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question id"));

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        //현재 로그인한 세션유저로 찾은 현재 유저 엔터티
        UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);

        //현재 스페이스가 내 스페이스라면
        if (space.isOwnedBy(user)) {
            assert user != null;
            model.addAttribute("sendingUserName", user.getName());
            model.addAttribute("sendingUserPicture", user.getPicture());
            model.addAttribute("userId", user.getId());
            model.addAttribute("questionId", question.getId());
            model.addAttribute("spaceId", space.getId());
        }

        return "answer";
    }


    // 답변 등록 API
    @PostMapping("/{spaceId}/{questionId}/answer/create")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId, @PathVariable Long spaceId, @RequestBody AnswerDTO answerDTO, HttpSession session) {

        try {
            SessionUser sessionUser = (SessionUser) session.getAttribute("user");

            // 답변달 질문 엔터티
            QuestionEntity question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question id"));

            // AnswerEntity 로 변환
            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);


            // id를 null 로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
            answerEntity.setId(null);

            answerEntity.setCreatedTime(LocalDateTime.now());

            // 서비스를 이용해 질문 엔티티를 생성한다
            List<AnswerEntity> entities = qaService.saveAnswer(answerEntity);

            question.setAnswers(entities);


            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());

            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();

            // ResponseDTO 를 리턴한다.
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            e.printStackTrace();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 내가 보낸 답변 삭제 API
    // 내 스페이스여야 함
    // 내가 보낸 답변이여야 함
    // Todo - 내가 보낸 답변만 삭제 가능해야 하는데 지금은 내 스페이스기만 하면 모든 답변 삭제 가능함
    @DeleteMapping("{spaceId}/{answerId}/answer/delete")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId, @PathVariable Long spaceId) {
        try {
            // 이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
            // 현재 로그인한 세션유저로 찾은 현재 유저 엔터티
            UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);

            // 현재 스페이스가 내 스페이스라면
            if (space.isOwnedBy(user)) {
                // 서비스를 이용해 답변 엔티티를 삭제한다
                assert user != null;
                qaService.deleteAnswer(answerId, user.getId());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("내 스페이스가 아니어서 삭제 불가능합니다.");
            }
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


//    @GetMapping("/{spaceId}/question")
//    public String questionInput(@PathVariable Long spaceId, Model model) {
//        // 이동한 스페이스 엔터티
//        SpaceEntity space = spaceRepository.findById(spaceId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
//
//        // 이동한 스페이스 주인아이디로 유저엔터티 찾기
//        UserEntity spaceUser = userRepository.findById(space.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
//
//        SessionUser user = (SessionUser) httpSession.getAttribute("user");
//        if (user != null) {
//            // 현재 로그인한 유저 엔터티
//            UserEntity userEntity = userRepository.findByEmail(user.getEmail()).orElse(null);
//            if (userEntity != null) {
//                // 현재 로그인한 유저의 스페이스 엔터티
//
//
//                model.addAttribute("sendingUserId", userEntity.getId());
//                model.addAttribute("sendingUserName", userEntity.getName());
//                model.addAttribute("sendingUserPicture", userEntity.getPicture());
//                // model.addAttribute("sendingUserSpaceId", space.getId());
//                model.addAttribute("receivingUserSpaceId", space.getId());
//
//            }
//
//        }
//        return "question";
//    }

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


                    // 질문 보내는 사람 이름 설정
                    questionEntity.setUserId(currentUser.getName());
                    // 질문보내는 사람 아이디 설정
                    questionEntity.setSendingUserId(currentUser.getId());

                    //답변 null 설정
                    questionEntity.setAnswers(null);
                    //현재 시각 설정
                    questionEntity.setCreatedTime(LocalDateTime.now());

                    //질문 보내는 사람 프사 설정
                    questionEntity.setSentUserPic(currentUser.getPicture());



                    //질문 받는 사람 아이디 설정
                    questionEntity.setReceivingUserId(spaceUser.getId());

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
            // 로그인을 안한 상태라면
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  //s
            }
        } else {
            return ResponseEntity.ok().body("질문을 하기 위해서는 로그인이 필수입니다.");
        }


    }


    // 질문 삭제 API - 남이 보낸 질문이라도 내 스페이스 내라면 삭제 가능
    @DeleteMapping("/{spaceId}/{questionId}/question/delete")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long spaceId, @PathVariable Long questionId) {
        try {
            // 이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
            // 현재 로그인한 세션유저로 찾은 현재 유저 엔터티
            UserEntity user = userRepository.findByEmail(sessionUser.getEmail()).orElse(null);

            // 현재 스페이스가 내 스페이스라면
            if (space.isOwnedBy(user)) {
                // 서비스를 이용해 질문 엔티티를 삭제한다
                qaService.deleteQuestionAndAnswers(questionId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("내 스페이스가 아니어서 삭제 불가능합니다.");
            }
        } catch (Exception e) {
            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
            String err = e.getMessage();
            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
}

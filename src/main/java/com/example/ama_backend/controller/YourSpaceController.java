//<<<<<<< HEAD
////package com.example.ama_backend.controller;
////
////import com.example.ama_backend.dto.AnswerDTO;
////import com.example.ama_backend.dto.QuestionDTO;
////import com.example.ama_backend.dto.ResponseDTO;
////import com.example.ama_backend.entity.AnswerEntity;
////import com.example.ama_backend.entity.QuestionEntity;
////import com.example.ama_backend.service.QAService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.List;
////import java.util.stream.Collectors;
////
////@RestController
////@RequestMapping("/your-space-id")
////public class YourSpaceController {
////    @Autowired
////    private QAService qaService;
////
////
////    // 질문 등록 API
////    @PostMapping("/question/create")
////    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO questionDTO,
////                                            @RequestParam(name = "anonymous", required = false, defaultValue = "false") boolean isAnonymous) {
////        try {
////            String temporaryUserId = "temporary-user";
////
////            // QuestionEntity 로 변환
////            QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);
////
////            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
////            questionEntity.setId(null);
////
////            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
////            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
////            questionEntity.setUserId(temporaryUserId);
////
////            // 서비스를 이용해 질문 엔티티를 생성한다
////            List<QuestionEntity> entities = qaService.saveQuestion(questionEntity, isAnonymous);
////
////            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
////            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
////
////            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
////            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
////
////            // ResponseDTO 를 리턴한다.
////            return ResponseEntity.ok().body(responseDTO);
////        } catch (Exception e) {
////            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
////            String err = e.getMessage();
////            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
////            return ResponseEntity.badRequest().body(responseDTO);
////        }
////    }
////
////    // 질답 조회 API
////    @GetMapping
////    public ResponseEntity<?> getMyAllQuestions() {
////        String temporaryUserId = "temporary-user";
////
////        // 서비스 메소드의 조회 메소드를 사용해서 질문 리스트를 가져온다
////        List<QuestionEntity> entities = qaService.getMyQuestions(temporaryUserId);
////
////        // 자바 스트림을 이용해 리턴된 엔터티 리스트를 DTO 리스트로 변환한다
////        List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
////
////        // 변환된 DTO 리스트를 이용해 ResponseDTO 를 초기화한다
////        ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
////
////        // ResponseDTO 리턴한다
////        return ResponseEntity.ok().body(responseDTO);
////    }
////
////    // 질문 삭제 API
////    @DeleteMapping("/question/delete")
////    public ResponseEntity<?> deleteQuestion(@RequestParam Long questionId) {
////        try {
////            String temporaryUserId = "temporary-user";
////
////            // 서비스를 이용해 질문 엔티티를 생성한다
////            List<QuestionEntity> entities = qaService.deleteQuestionAndAnswers(questionId);
////
////            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 리스트로 변환한다.
////            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
////
////            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
////            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
////
////            // ResponseDTO 를 리턴한다.
////            return ResponseEntity.ok().body(responseDTO);
////        } catch (Exception e) {
////            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
////            String err = e.getMessage();
////            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
////            return ResponseEntity.badRequest().body(responseDTO);
////        }
////    }
////
////
////    /**
////     *
////     * 질문에 답변 등록 API
////     * */
////    @PostMapping("/answer/create")
////    public ResponseEntity<?> createAnswer(@RequestBody AnswerDTO answerDTO,boolean isAnonymous) {
////        try {
////            String temporaryUserId = "temporary-user";
////
////            // AnswerEntity 로 변환
////            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);
////
////            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
////            answerEntity.setId(null);
////
////            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
////            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
////            answerEntity.setUserId(temporaryUserId);
////
////            // 서비스를 이용해 질문 엔티티를 생성한다
////            List<AnswerEntity> entities = qaService.saveAnswer(answerEntity,isAnonymous);
////
////            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
////            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());
////
////            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
////            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();
////
////            // ResponseDTO 를 리턴한다.
////            return ResponseEntity.ok().body(responseDTO);
////        } catch (Exception e) {
////            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
////            String err = e.getMessage();
////            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
////            return ResponseEntity.badRequest().body(responseDTO);
////        }
////    }
////
////    /**
////     * mock -
////     * 질문에 종속된 답변 삭제 API
////     * */
////    @DeleteMapping("/answer/delete")
////    public ResponseEntity<?> deleteAnswer(@RequestBody AnswerDTO answerDTO) {
////        try {
////            String temporaryUserId = "temporary-user";
////
////            // AnswerEntity 로 변환
////            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);
////
////            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
////            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
////            answerEntity.setUserId(temporaryUserId);
////
////            // 서비스를 이용해 답변 엔티티를 생성한다
////            List<AnswerEntity> entities = qaService.deleteAnswer(answerEntity);
////
////            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 AnswerDTO 리스트로 변환한다.
////            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());
////
////            // 변환된  AnswerDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
////            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();
////
////            // ResponseDTO 를 리턴한다.
////            return ResponseEntity.ok().body(responseDTO);
////        } catch (Exception e) {
////            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
////            String err = e.getMessage();
////            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
////            return ResponseEntity.badRequest().body(responseDTO);
////        }
////    }
////
////}
//=======
//package com.example.ama_backend.controller;
//
//import com.example.ama_backend.dto.AnswerDTO;
//import com.example.ama_backend.dto.QuestionDTO;
//import com.example.ama_backend.dto.ResponseDTO;
//import com.example.ama_backend.entity.AnswerEntity;
//import com.example.ama_backend.entity.QuestionEntity;
//import com.example.ama_backend.service.QAService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/your-space-id")
//public class YourSpaceController {
//    @Autowired
//    private QAService qaService;
//
//
//    // 질문 등록 API
//    @PostMapping("/question/create")
//    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO questionDTO,
//                                            @RequestParam(name = "anonymous", required = false, defaultValue = "false") boolean isAnonymous) {
//        try {
//            String temporaryUserId = "temporary-user";
//
//            // QuestionEntity 로 변환
//            QuestionEntity questionEntity = QuestionDTO.toEntity(questionDTO);
//
//            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
//            questionEntity.setId(null);
//
//            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
//            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
//            questionEntity.setUserId(temporaryUserId);
//
//            // 서비스를 이용해 질문 엔티티를 생성한다
//            List<QuestionEntity> entities = qaService.saveQuestion(questionEntity, isAnonymous);
//
//            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
//            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
//
//            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
//            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
//
//            // ResponseDTO 를 리턴한다.
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
//            String err = e.getMessage();
//            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//    }
//
//    // 질답 조회 API
//    @GetMapping
//    public ResponseEntity<?> getMyAllQuestions() {
//        String temporaryUserId = "temporary-user";
//
//        // 서비스 메소드의 조회 메소드를 사용해서 질문 리스트를 가져온다
//        List<QuestionEntity> entities = qaService.getMyQuestions(temporaryUserId);
//
//        // 자바 스트림을 이용해 리턴된 엔터티 리스트를 DTO 리스트로 변환한다
//        List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
//
//        // 변환된 DTO 리스트를 이용해 ResponseDTO 를 초기화한다
//        ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
//
//        // ResponseDTO 리턴한다
//        return ResponseEntity.ok().body(responseDTO);
//    }
//
//    // 질문 삭제 API
//    @DeleteMapping("/question/delete")
//    public ResponseEntity<?> deleteQuestion(@RequestParam Long questionId) {
//        try {
//            String temporaryUserId = "temporary-user";
//
//            // 서비스를 이용해 질문 엔티티를 생성한다
//            List<QuestionEntity> entities = qaService.deleteQuestionAndAnswers(questionId);
//
//            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 QuestionDTO 리스트로 변환한다.
//            List<QuestionDTO> dtos = entities.stream().map(QuestionDTO::new).collect(Collectors.toList());
//
//            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
//            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().data(dtos).build();
//
//            // ResponseDTO 를 리턴한다.
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
//            String err = e.getMessage();
//            ResponseDTO<QuestionDTO> responseDTO = ResponseDTO.<QuestionDTO>builder().error(err).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//    }
//
//
//    /**
//     *
//     * 질문에 답변 등록 API
//     * */
//    @PostMapping("/answer/create")
//    public ResponseEntity<?> createAnswer(@RequestBody AnswerDTO answerDTO,boolean isAnonymous) {
//        try {
//            String temporaryUserId = "temporary-user";
//
//            // AnswerEntity 로 변환
//            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);
//
//            // id를 null로 초기화한다. 생성 당시에는 id가 없어야 하기 때문이다.
//            answerEntity.setId(null);
//
//            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
//            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
//            answerEntity.setUserId(temporaryUserId);
//
//            // 서비스를 이용해 질문 엔티티를 생성한다
//            List<AnswerEntity> entities = qaService.saveAnswer(answerEntity,isAnonymous);
//
//            // 자바 스트림을 이요해 리턴된 엔티티 리스트를  QuestionDTO 로 변환한다.
//            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());
//
//            // 변환된 QuestionDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
//            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();
//
//            // ResponseDTO 를 리턴한다.
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            // 혹시 예외가 있으면 dto 대신 error 에 메시지를 넣어 리턴한다
//            String err = e.getMessage();
//            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//    }
//
//    /**
//     * mock -
//     * 질문에 종속된 답변 삭제 API
//     * */
//    @DeleteMapping("/answer/delete")
//    public ResponseEntity<?> deleteAnswer(@RequestBody AnswerDTO answerDTO) {
//        try {
//            String temporaryUserId = "temporary-user";
//
//            // AnswerEntity 로 변환
//            AnswerEntity answerEntity = AnswerDTO.toEntity(answerDTO);
//
//            // 임시 사용자 아이디를 설정해 준다. 나중에 인증과 인가를 통해 수정할 예정이다. 지금은 한 명의 사용자(temporary-user)만
//            // 로그인 없이 사용할 수 있는 애플리케이션인 셈이다.
//            answerEntity.setUserId(temporaryUserId);
//
//            // 서비스를 이용해 답변 엔티티를 생성한다
//            List<AnswerEntity> entities = qaService.deleteAnswer(answerEntity);
//
//            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 AnswerDTO 리스트로 변환한다.
//            List<AnswerDTO> dtos = entities.stream().map(AnswerDTO::new).collect(Collectors.toList());
//
//            // 변환된  AnswerDTO 리스트를 이용해 ResponseDTO 를 초기화한다.
//            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().data(dtos).build();
//
//            // ResponseDTO 를 리턴한다.
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            // 혹시 예외가 있으면 dto 대신 error에 메시지를 넣어 리턴한다
//            String err = e.getMessage();
//            ResponseDTO<AnswerDTO> responseDTO = ResponseDTO.<AnswerDTO>builder().error(err).build();
//            return ResponseEntity.badRequest().body(responseDTO);
//        }
//    }
//
//
//}
//>>>>>>> 167057c5e5be60867b2bd6dcad7ae005c5c2c25e

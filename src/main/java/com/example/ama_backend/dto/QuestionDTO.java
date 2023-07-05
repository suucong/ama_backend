package com.example.ama_backend.dto;

import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.entity.QuestionEntity;
import com.example.ama_backend.service.QAService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuestionDTO {

    private Long id;
    private String sentUserPic;

    private Long sendingUserId; // 질문자 고유 아이디
    private Long receivingUserId; // 질문받은 유저 고유 아이디


    private String receivedUserPic; //받은 유저의 사진
    private String receivedUserName; //받은 유저의 이름

    private String questionText;
    private LocalDateTime createdTime;
    private List<AnswerDTO> answers;
    private String userId; // 질문자 닉네임 또는 "익명"
    private Boolean isAnonymous; // "익명"으로 질문했는지 여부

    public QuestionDTO(QuestionEntity question) {
        this.id = question.getId();
        this.sentUserPic=question.getSentUserPic();
        this.sendingUserId=question.getSendingUserId();
        this.receivingUserId=question.getReceivingUserId();
        this.receivedUserName=question.getReceivedUserId();
        this.receivedUserPic=question.getReceivedUserPic();
        this.questionText = question.getQuestionText();
        this.createdTime = question.getCreatedTime();
        this.answers = question.getAnswers() != null
                ? question.getAnswers().stream().map(AnswerDTO::new).collect(Collectors.toList())
                : null;
        this.userId=question.getUserId();
        this.isAnonymous=question.getIsAnonymous();
    }

    public static QuestionEntity toEntity(final QuestionDTO dto){
        return QuestionEntity.builder()
                .id(dto.getId())
                .sentUserPic(dto.getSentUserPic())
                .sendingUserId(dto.getSendingUserId())
                .receivingUserId(dto.getReceivingUserId())
                .questionText(dto.getQuestionText())
                .receivedUserId(dto.getReceivedUserName())
                .receivedUserPic(dto.getReceivedUserPic())
                .createdTime(dto.getCreatedTime())
                .answers(dto.getAnswers() != null
                        ? dto.getAnswers().stream().map(AnswerDTO::toEntity).collect(Collectors.toList())
                        : null)
                .userId(dto.getUserId())
                .isAnonymous(dto.getIsAnonymous())
                .build();
    }
}


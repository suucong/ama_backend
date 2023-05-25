package com.example.ama_backend.dto;

import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.entity.QuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AnswerDTO {
    private Long id;//
    private Boolean isPublic;
    private String sentUserPic;
    private Long userId; // 답변자 고유 아이디
    private String userName; // 답변자 닉네임 또는 "익명"
    private String answerText;
    private String alternativeAnswerText; // "질문자만 볼 수 있는 답변입니다." 텍스트
    private LocalDateTime createdTime;
    private Long questionId;

    public AnswerDTO(AnswerEntity answerEntity) {
        this.id = answerEntity.getId();
        this.isPublic=answerEntity.getIsPublic();
        this.sentUserPic=answerEntity.getSentUserPic();
        this.userId=answerEntity.getUserId();
        this.userName=answerEntity.getUserName();
        this.alternativeAnswerText=answerEntity.getAlternativeAnswerText();
        this.answerText = answerEntity.getAnswerText();
        this.createdTime = answerEntity.getCreatedTime();
        this.questionId = answerEntity.getQuestion().getId();
    }

    public static AnswerEntity toEntity(final AnswerDTO dto){
        return AnswerEntity.builder()
                .id(dto.getId())
                .isPublic(dto.getIsPublic())
                .sentUserPic(dto.getSentUserPic())
                .userName(dto.getUserName())
                .userId(dto.getUserId())
                .answerText(dto.getAnswerText())
                .alternativeAnswerText(dto.getAlternativeAnswerText())
                .createdTime(dto.getCreatedTime())
                .question(QuestionEntity.builder().id(dto.getQuestionId()).build())
                .build();
    }
}

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
    private Long id;
    private String answerText;
    private LocalDateTime createdTime;
    private Long questionId;

    public AnswerDTO(AnswerEntity answerEntity) {
        this.id = answerEntity.getId();
        this.answerText = answerEntity.getAnswerText();
        this.createdTime = answerEntity.getCreatedTime();
        this.questionId = answerEntity.getQuestion().getId();
    }

    public static AnswerEntity toEntity(final AnswerDTO dto){
        return AnswerEntity.builder()
                .id(dto.getId())
                .answerText(dto.getAnswerText())
                .createdTime(dto.getCreatedTime())
                .question(QuestionEntity.builder().id(dto.getQuestionId()).build())
                .build();
    }
}

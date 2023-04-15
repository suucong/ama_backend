package com.example.ama_backend.dto;

import com.example.ama_backend.entity.AnswerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AnswerDTO {
    private long id;
    private String nickName;
    private String answerText;
    private LocalDateTime createdTime;
    private QuestionDTO question;

    public AnswerDTO(AnswerEntity answerEntity) {
        this.id = answerEntity.getId();
        this.nickName = answerEntity.getNickName();
        this.answerText = answerEntity.getAnswerText();
        this.createdTime = answerEntity.getCreatedTime();
        this.question = new QuestionDTO(answerEntity.getQuestion());
    }
}

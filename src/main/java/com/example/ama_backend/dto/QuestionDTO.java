package com.example.ama_backend.dto;

import com.example.ama_backend.entity.QuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuestionDTO {
    private long id;
    private String nickName;
    private String questionText;
    private LocalDateTime createdTime;
    private List<AnswerDTO> answers;

    public QuestionDTO(QuestionEntity question) {
        this.id = question.getId();
        this.nickName = question.getNickName();
        this.questionText = question.getQuestionText();
        this.createdTime = question.getCreatedTime();
        this.answers = question.getAnswers().stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toList());
    }
}

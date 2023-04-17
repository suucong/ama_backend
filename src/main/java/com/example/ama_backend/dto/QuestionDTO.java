package com.example.ama_backend.dto;

import com.example.ama_backend.entity.AnswerEntity;
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
    private String id;
    //private String nickName;
    private String questionText;
    private LocalDateTime createdTime;
    private List<AnswerEntity> answers;

    public QuestionDTO(QuestionEntity question) {
        this.id = question.getId();
        //this.nickName = question.getNickName();
        this.questionText = question.getQuestionText();
        this.createdTime = question.getCreatedTime();
        this.answers = question.getAnswers();
    }

    public static QuestionEntity toEntity(final QuestionDTO dto){
        return QuestionEntity.builder()
                .id(dto.getId())
                .questionText(dto.getQuestionText())
                .createdTime(dto.getCreatedTime())
                .answers(dto.getAnswers())
                .build();
    }
}

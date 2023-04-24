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
    private Long id;
    private String questionText;
    private LocalDateTime createdTime;
    private List<AnswerDTO> answers;

    public QuestionDTO(QuestionEntity question) {
        this.id = question.getId();
        this.questionText = question.getQuestionText();
        this.createdTime = question.getCreatedTime();
        this.answers = question.getAnswers() != null
                ? question.getAnswers().stream().map(AnswerDTO::new).collect(Collectors.toList())
                : null;
    }

    public static QuestionEntity toEntity(final QuestionDTO dto){
        return QuestionEntity.builder()
                .id(dto.getId())
                .questionText(dto.getQuestionText())
                .createdTime(dto.getCreatedTime())
                .answers(dto.getAnswers() != null
                        ? dto.getAnswers().stream().map(AnswerDTO::toEntity).collect(Collectors.toList())
                        : null)
                .build();
    }
}


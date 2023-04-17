package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(generator ="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id; // 이 오브젝트의 아이디
    private String answerText; // 답변 내용
    private LocalDateTime createdTime; //답변이 올라온 시간
    private String nickname; // 유저 닉네임
    // @ManyToOne 어노테이션을 사용하여 QuestionEntity 클래스의 answers 프로퍼티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private QuestionEntity question; // QuestionEntity 에 대한 외래 키 참조
}

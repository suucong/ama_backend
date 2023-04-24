package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동 증가
    private Long id; // 이 오브젝트의 아이디
    @Column(length = 500, nullable = false)
    private String answerText; // 답변 내용
    private LocalDateTime createdTime; //답변이 올라온 시간
    private String userId; // 유저 닉네임
    // @ManyToOne 어노테이션을 사용하여 QuestionEntity 클래스의 answers 프로퍼티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private QuestionEntity question; // QuestionEntity 객체를 참조
}

package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
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

    private Long userId; // 유저 고유 아이디

    @Column(nullable = false)
    private String sentUserPic;

    @Column(length = 500, nullable = false)
    private String answerText; // 원본 답변 내용

    @Column(name = "alternative_answer_text")
    private String alternativeAnswerText; // "질문자만 볼 수 있는 답변입니다." 텍스트

    private LocalDateTime createdTime; //답변이 올라온 시간

    private String userName; // 유저 닉네임

    private Boolean isPublic; //답변 공개 여부

    // @ManyToOne 어노테이션을 사용하여 QuestionEntity 클래스의 answers 프로퍼티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private QuestionEntity question; // QuestionEntity 객체를 참조


    //내가 작성한 답변인지 판별하는 메소드
    public boolean isMyAnswer(final Long userId) {
        return Objects.equals(this.userId, userId);
    }

}

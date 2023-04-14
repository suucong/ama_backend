package com.example.ama.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // 질문의 고유 ID
    private String nickName;  // 유저의 닉네임
    private String answerText; // 답변 내용
    private LocalDateTime createdTime; //답변이 올라온 시간
    // @ManyToOne 어노테이션을 사용하여 QuestionEntity 클래스의 answers 프로퍼티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private QuestionEntity question; // QuestionEntity에 대한 외래 키 참조
    //생성자
    public AnswerEntity(long id, String nickName, String answerText, LocalDateTime createdTime) {
        this.id = id;
        this.nickName = nickName;
        this.answerText = answerText;
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAnswerText() {
        return answerText;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}

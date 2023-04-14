package com.example.ama_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // 질문의 고유 ID
    private String nickName;  // 익명 유저의 닉네임
    private String questionText; // 질문 내용
    private LocalDateTime createdTime;  // 질문이 올라온 시간
    // @OneToMany 어노테이션을 사용하여 AnswerEntity 클래스의 question 프로퍼티와 매핑하겠습니다.
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<AnswerEntity> answers = new ArrayList<>(); //종속된 답변 리스트

    public QuestionEntity(long id, String nickName, String questionText, LocalDateTime createdTime) {
        this.id = id;
        this.nickName = nickName;
        this.questionText = questionText;
        this.createdTime = createdTime;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setAnswers(List<AnswerEntity> answers) {
        this.answers = answers;
    }


    public void setId(long id) {
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public List<AnswerEntity> getAnswers() {
        return answers;
    }

    public void addAnswer(AnswerEntity answer) {
        answers.add(answer);
    }
}

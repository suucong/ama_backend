package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "questions")
public class QuestionEntity {
    @Id     // 이 Entity의 기본 키 필드 ID를 정의
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 생성해주는 코드
    private Long id; // 이 오브젝트의 아이디

    @Column(nullable = false)
    private String sentUserPic;

    @Column(nullable = false)
    private String userId; // "익명" 혹은 유저네임

    @Column(nullable = false)
    private Long sendingUserId;  // 질문하는 유저의 아이디

    @Column(nullable = false)
    private Long receivingUserId;  // 질문받는 유저의 아이디

    @Column(length = 500, nullable = false)
    private String questionText; // 질문 내용

    private LocalDateTime createdTime;  // 질문이 올라온 시간

    @Column(nullable = false)
    private Boolean isAnonymous; // "익명"으로 질문했는지 여부

    // @OneToMany 어노테이션을 사용하여 AnswerEntity 클래스의 question 프로퍼티와 매핑하겠습니다.
    @OneToMany(mappedBy = "question",
            //질문 삭제되면 답변 또한 삭제돼야 한다,
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY)
    @Builder.Default
    @Column(nullable = true)
    private List<AnswerEntity> answers=new ArrayList<>();//종속된 답변 리스트

    //내가 작성한 질문인지 판별하는 메소드
    public boolean isMyQuestion(final Long userId) {
        return Objects.equals(this.sendingUserId, userId);
    }

}

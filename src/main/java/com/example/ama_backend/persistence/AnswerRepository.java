package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 JpaRepository 를 상속하면 기본적인 CRUD 메소드를 자동 생성해주기 때문에 별도의 코드 작성 필요없음
 이후 서비스 클래스에서 해당 메소드들을 호출하여 데이터베이스와 상호작용하면 됨
 */
@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity,Long> {
    List<AnswerEntity> findByUserId(Long Id); // 유저의 남긴 답변 조회
}

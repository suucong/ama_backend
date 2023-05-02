package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.QuestionEntity;
import com.example.ama_backend.entity.SpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<SpaceEntity, Long> {

    Optional<SpaceEntity> findByIdAndUserId(Long id, Long userId);

    Optional<SpaceEntity> findByUserId(Long userId);

    // 보낸 질문 검색
    List<QuestionEntity> findQuestionsByQuestionsAndUserId(SpaceEntity space,Long userId); // (보낸질문의 대상이 되는 스페이스, 조회하려는 유저의 ID)
    // 받은 질문 검색
    List<QuestionEntity> findReceivedQuestionsBySpaceAndUserId(SpaceEntity toSpace,Long userId);
}

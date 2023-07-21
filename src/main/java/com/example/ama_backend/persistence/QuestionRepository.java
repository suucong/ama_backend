package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.QuestionEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 JpaRepository 를 상속하면 기본적인 CRUD 메소드를 자동 생성해주기 때문에 별도의 코드 작성 필요없음
 이후 서비스 클래스에서 해당 메소드들을 호출하여 데이터베이스와 상호작용하면 됨
*/

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    default List<QuestionEntity> findLastQuestionsByUserId(Long userId, int size, int page) {
        // 제일 마지막 순서의 질문 리스트를 가져오도록 id를 기준으로 내림차순 정렬(Sort) 설정
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // PageRequest를 이용하여 페이징 처리, size 개수만큼 가져오도록 설정
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // 질문을 receivingUserId를 기준으로 가져옴
        return findByReceivingUserId(userId, pageRequest);
    }
    default List<QuestionEntity> findLastQuestionsBySendingUserId(Long sUserId, int size, int page) {
        // 제일 마지막 순서부터 질문 리스트를 가져오도록 id를 기준으로 내림차순 정렬 설정
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // PageRequest를 이용하여 페이징 처리, size 개수만큼 가져오도록 설정
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return findBySendingUserId(sUserId, pageRequest);
    }
    List<QuestionEntity> findByReceivingUserId(Long id, PageRequest pageRequest);
    List<QuestionEntity> findBySendingUserId(Long id, PageRequest pageRequest);
    List<QuestionEntity> findByReceivingUserId(Long id); // 내가 받은 질문 조회하는 메소드
    List<QuestionEntity> findBySendingUserId(Long id); // 내가 받은 질문 조회하는 메소드
}

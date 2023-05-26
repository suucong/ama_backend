package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.SpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<SpaceEntity, Long> {
    Optional<SpaceEntity> findByIdAndUserId(Long id, Long userId);

    Optional<SpaceEntity> findByUserId(Long userId); // 주인 유저 고유 아이디로 스페이스엔티티 찾는 메소드
}

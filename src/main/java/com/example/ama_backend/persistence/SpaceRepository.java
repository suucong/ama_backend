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
}

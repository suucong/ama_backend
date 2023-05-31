package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.Follow;
import com.example.ama_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 팔로우 리스트
    List<Follow> findByFromUser(UserEntity fromUser);

    // 팔로워 리스트
    List<Follow> findByToUser(UserEntity toUser);

    // 팔로우, 언팔로우 유무
    Optional<Follow> findByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);

    // unFollow
    @Modifying
    @Transactional
    public void deleteByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);
}

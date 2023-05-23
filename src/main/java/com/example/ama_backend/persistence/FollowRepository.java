package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 리스트
    public List<Follow> findByFromUserId(Long fromUser);

    // 팔로워 리스트
    public List<Follow> findByToUserId(Long toUser);

    // 팔로우, 언팔로우 유무
    @Query(value = "select count(*) from follow where FROM_USER = ?1 and TO_USER = ?2", nativeQuery = true)
    public int findByFromUserIdAndToUserId(Long fromUser, Long toUser);

    // unFollow
    @Modifying
    @Transactional
    public void deleteByFromUserIdAndToUserId(Long fromUser, Long toUser);
}

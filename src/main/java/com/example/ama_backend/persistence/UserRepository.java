package com.example.ama_backend.persistence;

import com.example.ama_backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// UserEntity 엔티티에 대한 SpringData JPA 저장소를 정의하고 메일 주소로 사용자를 찾는 추가 방법을 포함
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // 소셜 로그인으로 반환되는 값 중 email을 통해 이미 생성된 사용자인지 처음 가입하는 사용자인지 판단하기 위한 메소드
    Optional<UserEntity> findByEmail(String email);

//    Boolean existsByEmail(String email);
//    UserEntity findByEmailAndPassword(String email,String password);
}

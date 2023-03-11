package com.example.demo.persistence;

import com.example.demo.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JpaRepository 는 인터페이스다. 이걸 사용하려면 새 인터페이스를 작성해 extends 해야 한다.
 * Repository package 가 DAO 다.
 */
@Repository
public interface TodoRepository extends JpaRepository<TodoEntity,String> {

    // Todo리스트 반환(추가, 검색  )
    List<TodoEntity> findByUserId(String userId);
}

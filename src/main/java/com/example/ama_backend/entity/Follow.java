package com.example.ama_backend.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fromUser")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name="toUser")
    private UserEntity toUser;

    @Builder
    public Follow(Long id, UserEntity fromUser, UserEntity toUser) {
        this.id=id;
        this.fromUser=fromUser;
        this.toUser=toUser;
    }
}


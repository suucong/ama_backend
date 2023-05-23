package com.example.ama_backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="fromUser")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name="toUser")
    private UserEntity toUser;
}


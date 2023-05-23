package com.example.ama_backend.entity;


import com.example.ama_backend.entity.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Column(nullable = true)
    private String profileImgName = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = true)
    private String instaId;

    @Column(length = 100,nullable = true)
    private String introduce;

    @Builder
    public UserEntity(Long id, String name, String email, String picture, String profileImgName, String introduce, String instaId, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.profileImgName = profileImgName;
        this.role = role;
        this.introduce = introduce;
        this.instaId = instaId;
    }

    public UserEntity update(Long id,String name, String picture, String profileImgName, String introduce, String instaId) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.profileImgName = profileImgName;
        this.introduce = introduce;
        this.instaId = instaId;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}

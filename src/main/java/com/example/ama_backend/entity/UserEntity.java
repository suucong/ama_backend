package com.example.ama_backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    private byte[] profileByte = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = true)
    private String instaId;

    @Column(length = 100,nullable = true)
    private String introduce;

    @Column(nullable = true)
    private String link;

    @Column(nullable = false)
    private boolean stopSpace = false;

    @Column(nullable = false)
    private boolean alertSpace = false;

    @Column(nullable = false)
    private boolean isKakaoUser = false;

    @Builder
    public UserEntity(Long id, String name, String email, String picture, byte[] profileByte, String introduce, String instaId, Role role, String link, boolean stopSpace, boolean alertSpace, boolean isKakaoUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.profileByte = profileByte;
        this.role = role;
        this.introduce = introduce;
        this.instaId = instaId;
        this.link = link;
        this.stopSpace = stopSpace;
        this.alertSpace = alertSpace;
        this.isKakaoUser = isKakaoUser;
    }

    public UserEntity update(Long id,String name, String picture, byte[] profileByte, String introduce, String instaId, String link, boolean stopSpace, boolean alertSpace, boolean isKakaoUser) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.profileByte = profileByte;
        this.introduce = introduce;
        this.instaId = instaId;
        this.link = link;
        this.stopSpace = stopSpace;
        this.alertSpace = alertSpace;
        this.isKakaoUser = isKakaoUser;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}

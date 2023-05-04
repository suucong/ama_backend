package com.example.ama_backend.entity;


import com.example.ama_backend.entity.Role;
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

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 20, nullable = true)
    private String instaId = "suu";

    @Column(length = 70,nullable = true)
    private String introduce = "suucong";

    @Builder
    public UserEntity(Long id, String name, String email, String picture, String introduce, String instaId, Role role) {
        this.id = id;       // UserDTO생성할때 자꾸 오류가떠서 추가함(넣어주면안되나요??은영언니..)
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.introduce = introduce;
        this.instaId = instaId;
    }

    public UserEntity update(Long id,String name, String picture, String introduce, String instaId) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.introduce = introduce;
        this.instaId = instaId;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
